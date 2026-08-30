package com.routeoptimizer;

import java.util.Locale;

public class TrafficFallbackTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("        TRAFFIC FALLBACK TEST");
        System.out.println("========================================");
        System.out.println();

        Location p1 = new Location("P1", "Origin Point");
        Location p2 = new Location("P2", "Destination Point");

        // 1. Test Fallback Enabled
        TrafficConfiguration configFallbackOn = new TrafficConfiguration(
                TrafficSourceMode.LIVE, "https://invalid.endpoint.test", null, 60000L, 2, true, 0.15
        );
        ExternalLiveTrafficProvider providerWithFallback = new ExternalLiveTrafficProvider(configFallbackOn, new TrafficCache());
        TrafficMetrics fallbackMetrics = providerWithFallback.getTraffic(p1, p2, System.currentTimeMillis());

        System.out.println("Test 1: Fallback Enabled Behavior:");
        System.out.println("  Reported Source: " + fallbackMetrics.getSource());
        System.out.printf("  Reported Multiplier: %.2fx%n", fallbackMetrics.getMultiplier());
        boolean test1 = fallbackMetrics.getSource().contains("SIMULATED FALLBACK");
        System.out.println("  Test 1 Result: " + (test1 ? "PASSED" : "FAILED"));
        System.out.println();

        // 2. Test Fallback Disabled (Strict Mode)
        TrafficConfiguration configStrict = new TrafficConfiguration(
                TrafficSourceMode.LIVE, "https://invalid.endpoint.test", null, 60000L, 2, false, 0.15
        );
        ExternalLiveTrafficProvider providerStrict = new ExternalLiveTrafficProvider(configStrict, new TrafficCache());

        boolean test2 = false;
        try {
            providerStrict.getTraffic(p1, p2, System.currentTimeMillis());
        } catch (IllegalStateException e) {
            System.out.println("Test 2: Strict Mode (Fallback Disabled) Error Caught:");
            System.out.println("  Message: " + e.getMessage());
            test2 = true;
        }

        System.out.println("  Test 2 Result: " + (test2 ? "PASSED" : "FAILED"));

        System.out.println();
        System.out.println("========================================");
        System.out.println("TRAFFIC FALLBACK: " + (test1 && test2 ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
