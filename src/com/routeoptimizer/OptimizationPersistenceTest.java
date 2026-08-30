package com.routeoptimizer;

import java.util.List;
import java.util.Locale;

public class OptimizationPersistenceTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("    OPTIMIZATION PERSISTENCE TEST");
        System.out.println("========================================");
        System.out.println();

        DatabaseManager db = new DatabaseManager(new DatabaseConfiguration(DatabaseConfiguration.DatabaseType.EMBEDDED_IN_MEMORY, null));
        FleetManagementService fleetService = new FleetManagementService(db);
        TrafficService trafficService = new TrafficService(db);
        OptimizationService optService = new OptimizationService(fleetService, trafficService, db);

        // 1. Build Request
        OptimizationRequest req = new OptimizationRequest();
        req.getDepots().add(new DepotDto("W1", "Hub 1", 51.5308, -0.1238));
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

        // 2. Run Optimization & verify persistence
        OptimizationResponse resp = optService.runOptimization(req);
        String optId = resp.getOptimizationId();

        System.out.println("Optimization ID: " + optId);
        System.out.println("Status: " + resp.getStatus());
        System.out.printf("Total Distance: %.2f km%n", resp.getTotalDistanceKm());

        // 3. Verify Database Entities
        OptimizationRunEntity runEntity = optService.getRunRepo().findById(optId);
        boolean t1 = runEntity != null && "COMPLETED".equals(runEntity.getStatus()) && runEntity.getRuntimeMs() != null;
        System.out.println("Test 1 (Optimization Run Persisted): " + (t1 ? "PASSED" : "FAILED"));

        OptimizationResultEntity resEntity = optService.getResultRepo().findById(optId);
        boolean t2 = resEntity != null && resEntity.getTotalDistance() == resp.getTotalDistanceKm();
        System.out.println("Test 2 (Optimization Result Metrics Persisted): " + (t2 ? "PASSED" : "FAILED"));

        List<FleetRouteEntity> routes = optService.getRouteRepo().findByOptimizationId(optId);
        boolean t3 = routes.size() == resp.getVehicleRoutes().size();
        System.out.println("Test 3 (Fleet Routes Persisted - " + routes.size() + " routes): " + (t3 ? "PASSED" : "FAILED"));

        int totalStops = 0;
        for (FleetRouteEntity r : routes) {
            List<RouteStopEntity> stops = optService.getStopRepo().findByFleetRouteId(r.getId());
            totalStops += stops.size();
        }
        boolean t4 = (totalStops == 4); // 4 customers assigned
        System.out.println("Test 4 (Route Stops Sequence Persisted - " + totalStops + " stops): " + (t4 ? "PASSED" : "FAILED"));

        // 4. Retrieve by ID
        OptimizationResponse retrievedResp = optService.getOptimization(optId);
        boolean t5 = retrievedResp != null && "COMPLETED".equals(retrievedResp.getStatus())
                && retrievedResp.getOptimizationScore().equals(resp.getOptimizationScore());
        System.out.println("Test 5 (Get Optimization by ID from Database): " + (t5 ? "PASSED" : "FAILED"));

        boolean allPassed = t1 && t2 && t3 && t4 && t5;

        System.out.println();
        System.out.println("========================================");
        System.out.println("OPTIMIZATION PERSISTENCE: " + (allPassed ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
