package com.routeoptimizer;

import java.util.Locale;

public class OptimizationControllerTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("     OPTIMIZATION CONTROLLER TEST");
        System.out.println("========================================");
        System.out.println();

        DatabaseManager db = new DatabaseManager(new DatabaseConfiguration(DatabaseConfiguration.DatabaseType.EMBEDDED_IN_MEMORY, null));
        FleetManagementService fleetService = new FleetManagementService(db);
        TrafficService trafficService = new TrafficService(db);
        OptimizationService optService = new OptimizationService(fleetService, trafficService, db);
        OptimizationController controller = new OptimizationController(optService);

        // 1. Build Request
        OptimizationRequest req = new OptimizationRequest();
        req.getDepots().add(new DepotDto("W1", "Depot 1", 51.5308, -0.1238));
        req.getVehicles().add(new VehicleDto("V1", 60.0, "W1", 0.12, 10.0));
        req.getVehicles().add(new VehicleDto("V2", 60.0, "W1", 0.12, 10.0));

        for (int i = 1; i <= 4; i++) {
            req.getCustomers().add(new CustomerDto(
                    "C" + i, "Cust " + i, 51.50 + i * 0.01, -0.12 + i * 0.01, 15.0, "MEDIUM", 5.0, 0.0, 180.0
            ));
        }

        req.setPopulationSize(30);
        req.setGenerations(50);
        req.setSeed(42L);

        // 2. Run Optimization
        OptimizationResponse resp = controller.runOptimization(req);
        System.out.println("Optimization ID: " + resp.getOptimizationId());
        System.out.println("Status: " + resp.getStatus());
        System.out.printf("Total Distance: %.2f km%n", resp.getTotalDistanceKm());
        System.out.printf("Total Travel Time: %.2f min%n", resp.getTotalTravelTimeMinutes());
        System.out.printf("Optimization Score: %.4f%n", resp.getOptimizationScore());
        System.out.println("Vehicle Routes Generated: " + resp.getVehicleRoutes().size());

        boolean test1 = "COMPLETED".equals(resp.getStatus())
                && resp.getUnassignedCount() == 0
                && resp.getDuplicateCount() == 0
                && resp.getTotalCapacityViolations() == 0;
        System.out.println("Test 1 (Run Optimization): " + (test1 ? "PASSED" : "FAILED"));
        System.out.println();

        // 3. Get Status
        OptimizationResponse fetched = controller.getOptimization(resp.getOptimizationId());
        boolean test2 = fetched != null && fetched.getOptimizationId().equals(resp.getOptimizationId());
        System.out.println("Test 2 (Get Optimization by ID): " + (test2 ? "PASSED" : "FAILED"));
        System.out.println();

        // 4. Dynamic Re-optimization
        TrafficUpdateRequest trafficUpdate = new TrafficUpdateRequest("W1", "C1", 1.0, 2.5, System.currentTimeMillis(), "REST_API_EVENT");
        OptimizationResponse reoptResp = controller.reoptimize(resp.getOptimizationId(), trafficUpdate);
        boolean test3 = reoptResp != null && "COMPLETED".equals(reoptResp.getStatus())
                && reoptResp.getUnassignedCount() == 0;
        System.out.println("Test 3 (Dynamic Re-optimization): " + (test3 ? "PASSED" : "FAILED"));

        boolean allPassed = test1 && test2 && test3;

        System.out.println();
        System.out.println("========================================");
        System.out.println("OPTIMIZATION CONTROLLER: " + (allPassed ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
