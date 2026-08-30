package com.routeoptimizer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSRMRoutingProvider implements RoutingProvider {

    private final String baseUrl;
    private final int timeoutSeconds;
    private final HttpClient httpClient;
    private final RoutingCache cache;
    private final HaversineRoutingProvider fallbackProvider;
    private final boolean fallbackAllowed;

    private static final Pattern DISTANCE_PATTERN = Pattern.compile("\"distance\"\\s*:\\s*([0-9.]+)");
    private static final Pattern DURATION_PATTERN = Pattern.compile("\"duration\"\\s*:\\s*([0-9.]+)");

    public OSRMRoutingProvider(String baseUrl, int timeoutSeconds, RoutingCache cache, boolean fallbackAllowed) {
        this.baseUrl = (baseUrl != null && !baseUrl.trim().isEmpty())
                ? baseUrl.trim().replaceAll("/+$", "")
                : RoutingConfiguration.DEFAULT_OSRM_URL;
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 5;
        this.cache = cache != null ? cache : new RoutingCache();
        this.fallbackAllowed = fallbackAllowed;
        this.fallbackProvider = new HaversineRoutingProvider(35.0);

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(this.timeoutSeconds))
                .build();
    }

    public OSRMRoutingProvider(String baseUrl, RoutingCache cache) {
        this(baseUrl, 5, cache, true);
    }

    public OSRMRoutingProvider(RoutingCache cache) {
        this(RoutingConfiguration.DEFAULT_OSRM_URL, 5, cache, true);
    }

    public OSRMRoutingProvider() {
        this(RoutingConfiguration.DEFAULT_OSRM_URL, 5, new RoutingCache(), true);
    }

    @Override
    public RouteMetrics getRoute(GeoLocation origin, GeoLocation destination) {
        if (origin == null || destination == null) {
            return new RouteMetrics(0.001, 0.001);
        }

        // 1. Check Cache
        RouteMetrics cached = cache.get(origin, destination);
        if (cached != null) {
            return cached;
        }

        // 2. Query OSRM
        try {
            // OSRM format: /route/v1/driving/{lon1},{lat1};{lon2},{lat2}?overview=false
            String url = String.format(Locale.US,
                    "%s/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=false",
                    baseUrl,
                    origin.getLongitude(), origin.getLatitude(),
                    destination.getLongitude(), destination.getLatitude());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .header("User-Agent", "QuantumRouteOptimizer/3.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body().contains("\"code\":\"Ok\"")) {
                String body = response.body();
                double distMeters = extractDouble(DISTANCE_PATTERN, body);
                double durSeconds = extractDouble(DURATION_PATTERN, body);

                double distKm = distMeters / 1000.0;
                double timeMin = durSeconds / 60.0;

                RouteMetrics metrics = new RouteMetrics(distKm, timeMin, "osrm_real");
                cache.put(origin, destination, metrics);
                return metrics;
            } else {
                if (fallbackAllowed) {
                    RouteMetrics fb = fallbackProvider.getRoute(origin, destination);
                    cache.put(origin, destination, fb);
                    return fb;
                }
                throw new IllegalStateException("OSRM query failed with status " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            if (fallbackAllowed) {
                RouteMetrics fb = fallbackProvider.getRoute(origin, destination);
                cache.put(origin, destination, fb);
                return fb;
            }
            throw new RuntimeException("Failed to query OSRM routing service at " + baseUrl + ": " + e.getMessage(), e);
        }
    }

    @Override
    public double getDistance(GeoLocation origin, GeoLocation destination) {
        return getRoute(origin, destination).getDistanceKm();
    }

    @Override
    public double getTravelTime(GeoLocation origin, GeoLocation destination) {
        return getRoute(origin, destination).getTravelTimeMinutes();
    }

    @Override
    public String getProviderName() {
        return "OSRMRoutingProvider [" + baseUrl + "]";
    }

    @Override
    public boolean isAvailable() {
        try {
            // Quick test ping
            GeoLocation p1 = new GeoLocation("P1", 37.7749, -122.4194);
            GeoLocation p2 = new GeoLocation("P2", 37.7750, -122.4180);
            RouteMetrics m = getRoute(p1, p2);
            return m != null && m.getDistanceKm() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public RoutingCache getCache() {
        return cache;
    }

    private double extractDouble(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0.0;
    }
}
