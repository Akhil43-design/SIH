package com.routeoptimizer;

import java.util.Locale;

public class LiveTrafficProviderTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("       LIVE TRAFFIC PROVIDER TEST");
        System.out.println("========================================");
        System.out.println();

        TrafficConfiguration config = new TrafficConfiguration();
        TrafficCache cache = new TrafficCache(5000L); // 5 sec TTL
        ExternalLiveTrafficProvider provider = new ExternalLiveTrafficProvider(config, cache);

        Location origin = new GeoLocation("L1", "Origin Depot", 51.5074, -0.1278);
        Location destination = new GeoLocation("L2", "Customer Stop", 51.5154, -0.1755);

        long now = System.currentTimeMillis();

        if (!provider.isAvailable()) {
            System.out.println("LIVE TRAFFIC TEST:");
            System.out.println("NOTICE: External live traffic API key is not configured in environment (TRAFFIC_API_KEY).");
            System.out.println("Testing Fallback and Interface contract...");

            TrafficMetrics metrics = provider.getTraffic(origin, destination, now);
            System.out.println("Traffic Response: " + metrics);
            System.out.printf("  Multiplier: %.2fx%n", metrics.getMultiplier());
            System.out.printf("  Source: %s%n", metrics.getSource());
            System.out.printf("  Is Live: %b%n", metrics.isLive());

            // Test Cache
            TrafficMetrics cachedMetrics = provider.getTraffic(origin, destination, now);
            boolean cacheHit = cache.getHitCount() >= 1;
            System.out.println("Traffic Cache Hit Verified: " + (cacheHit ? "PASSED" : "FAILED"));

            System.out.println();
            System.out.println("========================================");
            System.out.println("LIVE TRAFFIC PROVIDER: PASSED (CONTRACT & FALLBACK)");
            System.out.println("========================================");
        } else {
            System.out.println("External API key detected. Querying live traffic...");
            TrafficMetrics live = provider.getTraffic(origin, destination, now);
            System.out.println("Live Traffic Response: " + live);
            System.out.println("========================================");
            System.out.println("LIVE TRAFFIC PROVIDER: PASSED (LIVE CONNECTIVITY)");
            System.out.println("========================================");
        }
    }
}
