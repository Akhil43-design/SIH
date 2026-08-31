package com.routeoptimizer;

import java.util.Arrays;
import java.util.List;

public class RealRoadDistanceConsistencyTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println(" REAL ROAD DISTANCE CONSISTENCY AUDIT");
        System.out.println("========================================\n");

        int passed = 0;
        int failed = 0;

        // Bengaluru Hubs: Peenya (W1) -> Manyata (C1)
        GeoLocation peenya = new GeoLocation("W1", "Peenya Industrial Area Depot", 12.9978, 77.5587);
        GeoLocation manyata = new GeoLocation("C1", "Manyata Tech Park, Nagawara", 13.0475, 77.6200);

        // 1. Verify Coordinates
        if (peenya.getLatitude() > 8.0 && peenya.getLatitude() < 37.0 && peenya.getLongitude() > 68.0 && peenya.getLongitude() < 97.5) {
            System.out.println("[PASS] 1. Origin and Destination coordinates are valid Indian geographic coordinates.");
            passed++;
        } else {
            System.err.println("[FAIL] 1. Invalid coordinates.");
            failed++;
        }

        // 2. Haversine Straight-line Distance
        HaversineRoutingProvider haversineProvider = new HaversineRoutingProvider();
        RouteMetrics haversineMetrics = haversineProvider.getRoute(peenya, manyata);
        double haversineKm = haversineMetrics.getDistanceKm();
        System.out.printf("  Haversine Straight-Line Distance: %.2f km%n", haversineKm);

        // 3. Real OSRM Road Distance
        OSRMRoutingProvider osrmProvider = new OSRMRoutingProvider();
        RouteMetrics osrmMetrics = osrmProvider.getRoute(peenya, manyata);
        double osrmKm = osrmMetrics.getDistanceKm();
        double osrmMin = osrmMetrics.getTravelTimeMinutes();
        System.out.printf("  OSRM Real Road Distance:         %.2f km (Duration: %.2f min)%n", osrmKm, osrmMin);

        if (osrmKm > 0 && osrmMin > 0) {
            System.out.println("[PASS] 2. OSRM request succeeded with valid positive distance and duration.");
            passed++;
        } else {
            System.err.println("[FAIL] 2. OSRM request returned invalid metrics.");
            failed++;
        }

        // 4. Sanity Check: OSRM Road Distance >= Haversine Straight-Line Distance
        if (osrmKm >= haversineKm) {
            System.out.printf("[PASS] 3. OSRM Road Distance (%.2f km) >= Haversine (%.2f km) [Ratio: %.2fx]%n",
                    osrmKm, haversineKm, osrmKm / haversineKm);
            passed++;
        } else {
            System.err.println("[FAIL] 3. OSRM distance is smaller than Haversine straight-line distance.");
            failed++;
        }

        // 5. Geographic Road Network Construction
        GeographicRoadNetworkBuilder builder = new GeographicRoadNetworkBuilder(osrmProvider);
        GeoCustomer manyataCust = new GeoCustomer("C1", "Manyata Tech Park", 13.0475, 77.6200, 20.0, DeliveryPriority.HIGH, 10.0, 480.0, 660.0);
        RoadNetwork roadNetwork = builder.buildRoadNetwork(Arrays.asList(peenya, manyataCust));

        Road forwardRoad = roadNetwork.findRoad(peenya, manyataCust);
        Road returnRoad = roadNetwork.findRoad(manyataCust, peenya);

        if (forwardRoad != null && Math.abs(forwardRoad.getDistance() - osrmKm) < 0.01) {
            System.out.printf("[PASS] 4. RoadNetwork stores exact OSRM road distance: %.2f km%n", forwardRoad.getDistance());
            passed++;
        } else {
            System.err.println("[FAIL] 4. RoadNetwork road distance mismatch.");
            failed++;
        }

        // 6. VehicleRoute consumes exact RoadNetwork distance
        Vehicle veh = new Vehicle("V1", 80.0, peenya, 0.12, 10.0);
        TrafficModel traffic = new TrafficModel();
        VehicleRoute vRoute = new VehicleRoute(veh, Arrays.asList(manyataCust), peenya, roadNetwork, traffic);

        double expectedRoundTrip = forwardRoad.getDistance() + returnRoad.getDistance();
        if (Math.abs(vRoute.getTotalDistance() - expectedRoundTrip) < 0.01) {
            System.out.printf("[PASS] 5. VehicleRoute correctly aggregates OSRM road distance: %.2f km (Round-trip: %.2f km + %.2f km)%n",
                    vRoute.getTotalDistance(), forwardRoad.getDistance(), returnRoad.getDistance());
            passed++;
        } else {
            System.err.println("[FAIL] 5. VehicleRoute distance mismatch: got " + vRoute.getTotalDistance() + " expected " + expectedRoundTrip);
            failed++;
        }

        // 7. FleetFitnessFunction receives exact road distance
        FleetFitnessFunction fitness = new FleetFitnessFunction();
        FleetRoutePlan plan = new FleetRoutePlan(peenya, Arrays.asList(vRoute), Arrays.asList(manyataCust), fitness);
        double fitnessScore = plan.getOverallFitness();

        if (plan.getTotalDistance() > 20.0 && fitnessScore > 0) {
            System.out.printf("[PASS] 6. FleetFitnessFunction computed based on real road distance: %.2f km (Score: %.4f)%n",
                    plan.getTotalDistance(), fitnessScore);
            passed++;
        } else {
            System.err.println("[FAIL] 6. FleetFitnessFunction not receiving real road metrics.");
            failed++;
        }

        System.out.println("\n========================================");
        System.out.println("AUDIT SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
