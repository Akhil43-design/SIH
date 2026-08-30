package com.routeoptimizer;

import java.util.List;

public class IndianCityDatasetTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   INDIAN CITY QIGA OPTIMIZATION TEST");
        System.out.println("========================================\n");

        int passed = 0;
        int failed = 0;

        String[] testCities = {"bengaluru", "hyderabad", "mumbai", "delhi", "chennai", "pune"};

        DatabaseManager db = new DatabaseManager();
        FleetManagementService fleetService = new FleetManagementService(db);
        TrafficService trafficService = new TrafficService();
        OptimizationService optService = new OptimizationService(fleetService, trafficService, db);

        for (String cityId : testCities) {
            System.out.println("--- Testing QIGA Fleet Optimization for: " + cityId.toUpperCase() + " ---");
            IndianCityDatasets.CityDataset ds = fleetService.loadCityDataset(cityId);

            OptimizationRequest req = new OptimizationRequest();
            req.setGenerations(50);
            req.setPopulationSize(30);
            req.setSeed(42L);

            long t0 = System.currentTimeMillis();
            OptimizationResponse resp = optService.runOptimization(req);
            long elapsed = System.currentTimeMillis() - t0;

            System.out.println("  Status: " + resp.getStatus());
            System.out.println("  Optimization Score: " + String.format("%.4f", resp.getOptimizationScore()));
            System.out.println("  Total Distance: " + String.format("%.2f", resp.getTotalDistanceKm()) + " km");
            System.out.println("  Total Travel Time: " + String.format("%.2f", resp.getTotalTravelTimeMinutes()) + " min");
            System.out.println("  Vehicle Routes: " + resp.getVehicleRoutes().size());
            System.out.println("  Execution Time: " + elapsed + " ms");

            if ("COMPLETED".equals(resp.getStatus()) && resp.getTotalDistanceKm() > 0 && !resp.getVehicleRoutes().isEmpty()) {
                System.out.println("[PASS] " + cityId.toUpperCase() + " QIGA optimization succeeded.\n");
                passed++;
            } else {
                System.err.println("[FAIL] " + cityId.toUpperCase() + " QIGA optimization failed.\n");
                failed++;
            }
        }

        // Reset to default Bengaluru
        fleetService.loadCityDataset("bengaluru");

        System.out.println("========================================");
        System.out.println("SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
