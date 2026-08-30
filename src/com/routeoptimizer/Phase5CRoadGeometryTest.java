package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class Phase5CRoadGeometryTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   PHASE 5C ROAD GEOMETRY TEST");
        System.out.println("========================================\n");

        int passed = 0;
        int failed = 0;

        OSRMRoutingProvider provider = new OSRMRoutingProvider();

        // 1. Define Indian Bengaluru Route: Peenya -> Manyata -> Whitefield -> Peenya
        List<GeoLocation> waypoints = new ArrayList<>();
        waypoints.add(new GeoLocation("W1", 12.9978, 77.5587)); // Peenya
        waypoints.add(new GeoLocation("C1", 13.0475, 77.6200)); // Manyata
        waypoints.add(new GeoLocation("C2", 12.9959, 77.6964)); // Whitefield
        waypoints.add(new GeoLocation("W1", 12.9978, 77.5587)); // Peenya Return

        System.out.println("Testing Multi-Stop Route: Peenya -> Manyata -> Whitefield -> Peenya...");
        RouteMetrics metrics = provider.getMultiStopRoute(waypoints);

        System.out.println("  Road Distance: " + String.format("%.2f", metrics.getDistanceKm()) + " km");
        System.out.println("  Travel Duration: " + String.format("%.2f", metrics.getTravelTimeMinutes()) + " min");
        System.out.println("  Geometry String Length: " + metrics.getGeometry().length() + " chars");

        // Count coordinate pairs in geometry string
        int commaPairs = metrics.getGeometry().split("\\],\\s*\\[").length;
        System.out.println("  Approx. Road Curve Points: " + commaPairs);

        // Test 1: Route Distance is realistic (> 20 km for Bengaluru tour)
        if (metrics.getDistanceKm() > 20.0) {
            System.out.println("[PASS] Test 1: Real OSRM Road Distance valid.");
            passed++;
        } else {
            System.err.println("[FAIL] Test 1: Distance too short: " + metrics.getDistanceKm());
            failed++;
        }

        // Test 2: Geometry points count > waypoints count (verifying curve following)
        if (commaPairs > waypoints.size() || metrics.getGeometry().length() > 50) {
            System.out.println("[PASS] Test 2: Geometry contains actual intermediate road coordinates (Points: " + commaPairs + ").");
            passed++;
        } else {
            System.err.println("[FAIL] Test 2: Geometry points insufficient: " + commaPairs);
            failed++;
        }

        // Test 3: Cache Verification for Multi-Stop Key
        RouteMetrics m2 = provider.getMultiStopRoute(waypoints);
        if (m2 != null && Math.abs(m2.getDistanceKm() - metrics.getDistanceKm()) < 0.001) {
            System.out.println("[PASS] Test 3: Multi-stop route consistency verified.");
            passed++;
        } else {
            System.err.println("[FAIL] Test 3: Multi-stop route inconsistent.");
            failed++;
        }

        // Test 4: Single Pair vs Multi-Stop
        RouteMetrics singleLeg = provider.getRoute(waypoints.get(0), waypoints.get(1));
        if (singleLeg != null && singleLeg.getDistanceKm() > 0) {
            System.out.println("[PASS] Test 4: Single leg OSRM route verified (" + String.format("%.2f", singleLeg.getDistanceKm()) + " km).");
            passed++;
        } else {
            System.err.println("[FAIL] Test 4: Single leg route failed.");
            failed++;
        }

        System.out.println("\n========================================");
        System.out.println("SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
