package com.routeoptimizer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalLiveTrafficProvider implements TrafficDataProvider {

    private final TrafficConfiguration config;
    private final TrafficCache cache;
    private final SimulatedTrafficProvider fallbackProvider;
    private final HttpClient httpClient;

    private static final Pattern CURRENT_SPEED_PATTERN = Pattern.compile("\"currentSpeed\"\\s*:\\s*([0-9.]+)");
    private static final Pattern FREE_FLOW_SPEED_PATTERN = Pattern.compile("\"freeFlowSpeed\"\\s*:\\s*([0-9.]+)");
    private static final Pattern CURRENT_TRAVEL_TIME_PATTERN = Pattern.compile("\"currentTravelTime\"\\s*:\\s*([0-9.]+)");

    public ExternalLiveTrafficProvider(TrafficConfiguration config, TrafficCache cache) {
        this.config = config != null ? config : new TrafficConfiguration();
        this.cache = cache != null ? cache : new TrafficCache(this.config.getCacheTtlMillis());
        this.fallbackProvider = new SimulatedTrafficProvider();

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(this.config.getTimeoutSeconds()))
                .build();
    }

    public ExternalLiveTrafficProvider(String apiKey) {
        this(TrafficConfiguration.createLiveWithFallback(apiKey), new TrafficCache());
    }

    public ExternalLiveTrafficProvider() {
        this(new TrafficConfiguration(), new TrafficCache());
    }

    @Override
    public TrafficMetrics getTraffic(Location origin, Location destination, long timestampMillis) {
        if (origin == null || destination == null) {
            return TrafficMetrics.createSimulated(1.0, 10.0, timestampMillis);
        }

        // 1. Check TTL Cache
        TrafficMetrics cached = cache.get(origin, destination, timestampMillis);
        if (cached != null) {
            return cached;
        }

        // 2. Check if API credentials exist
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            if (config.isFallbackEnabled()) {
                TrafficMetrics sim = fallbackProvider.getTraffic(origin, destination, timestampMillis);
                TrafficMetrics fb = new TrafficMetrics(sim.getMultiplier(), sim.getSpeedKmh(), sim.getDelayMinutes(),
                        timestampMillis, "SIMULATED FALLBACK (NO API KEY)", false);
                cache.put(origin, destination, timestampMillis, fb);
                return fb;
            }
            throw new IllegalStateException("Live traffic requested but TRAFFIC_API_KEY is not configured and fallback is disabled.");
        }

        // 3. Make Live Request (e.g. TomTom Flow Segment API or configured endpoint)
        try {
            double originLat = 51.5074, originLon = -0.1278;
            double destLat = 51.5074, destLon = -0.1278;

            if (origin instanceof GeoLocation) {
                GeoLocation g = (GeoLocation) origin;
                originLat = g.getLatitude();
                originLon = g.getLongitude();
            }
            if (destination instanceof GeoLocation) {
                GeoLocation g = (GeoLocation) destination;
                destLat = g.getLatitude();
                destLon = g.getLongitude();
            }

            // TomTom Flow Segment Data URL structure
            String url = String.format(Locale.US,
                    "%s/json/absolute/10/json?point=%.6f,%.6f&key=%s",
                    config.getApiEndpoint(),
                    originLat, originLon, apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(config.getTimeoutSeconds()))
                    .header("User-Agent", "QuantumRouteOptimizer/3.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body().contains("flowSegmentData")) {
                String body = response.body();
                double currentSpeed = extractDouble(CURRENT_SPEED_PATTERN, body, 30.0);
                double freeFlowSpeed = extractDouble(FREE_FLOW_SPEED_PATTERN, body, 50.0);
                double currentTravelTime = extractDouble(CURRENT_TRAVEL_TIME_PATTERN, body, 60.0);

                double multiplier = Math.max(0.80, freeFlowSpeed / Math.max(5.0, currentSpeed));
                double delayMin = Math.max(0.0, currentTravelTime / 60.0);

                TrafficMetrics liveMetrics = new TrafficMetrics(
                        multiplier, currentSpeed, delayMin, timestampMillis, "TOMTOM_LIVE_API", true
                );
                cache.put(origin, destination, timestampMillis, liveMetrics);
                return liveMetrics;
            } else {
                if (config.isFallbackEnabled()) {
                    TrafficMetrics sim = fallbackProvider.getTraffic(origin, destination, timestampMillis);
                    TrafficMetrics fb = new TrafficMetrics(sim.getMultiplier(), sim.getSpeedKmh(), sim.getDelayMinutes(),
                            timestampMillis, "SIMULATED FALLBACK (HTTP " + response.statusCode() + ")", false);
                    cache.put(origin, destination, timestampMillis, fb);
                    return fb;
                }
                throw new IllegalStateException("Live traffic query failed with HTTP status " + response.statusCode());
            }
        } catch (Exception e) {
            if (config.isFallbackEnabled()) {
                TrafficMetrics sim = fallbackProvider.getTraffic(origin, destination, timestampMillis);
                TrafficMetrics fb = new TrafficMetrics(sim.getMultiplier(), sim.getSpeedKmh(), sim.getDelayMinutes(),
                        timestampMillis, "SIMULATED FALLBACK (" + e.getClass().getSimpleName() + ")", false);
                cache.put(origin, destination, timestampMillis, fb);
                return fb;
            }
            throw new RuntimeException("Live traffic request error: " + e.getMessage(), e);
        }
    }

    @Override
    public double getAdjustedTravelTime(Road road, double baseTravelTimeMinutes, long timestampMillis) {
        if (road == null) {
            return baseTravelTimeMinutes;
        }
        TrafficMetrics tm = getTraffic(road.getFrom(), road.getTo(), timestampMillis);
        double roadCongestionFactor = 1.0 + (road.getTrafficLevel() - 1) * 0.15;
        return baseTravelTimeMinutes * tm.getMultiplier() * roadCongestionFactor;
    }

    @Override
    public TrafficSourceMode getMode() {
        return TrafficSourceMode.LIVE;
    }

    @Override
    public String getSourceName() {
        return "ExternalLiveTrafficProvider [" + config.getApiEndpoint() + "]";
    }

    @Override
    public boolean isAvailable() {
        return config.getApiKey() != null && !config.getApiKey().trim().isEmpty();
    }

    public TrafficCache getCache() {
        return cache;
    }

    private double extractDouble(Pattern pattern, String text, double defaultValue) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (Exception ignored) {
            }
        }
        return defaultValue;
    }
}
