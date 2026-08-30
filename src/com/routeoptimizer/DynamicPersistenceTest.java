package com.routeoptimizer;

import java.util.List;
import java.util.Locale;

public class DynamicPersistenceTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("      DYNAMIC PERSISTENCE TEST");
        System.out.println("========================================");
        System.out.println();

        DatabaseManager db = new DatabaseManager(new DatabaseConfiguration(DatabaseConfiguration.DatabaseType.EMBEDDED_IN_MEMORY, null));
        FleetManagementService fleetService = new FleetManagementService(db);
        TrafficService trafficService = new TrafficService(db);
        OptimizationService optService = new OptimizationService(fleetService, trafficService, db);

        // 1. Initial Optimization
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

        OptimizationResponse initialResp = optService.runOptimization(req);
        String initialId = initialResp.getOptimizationId();
        System.out.println("Initial Optimization ID: " + initialId);

        // 2. Injected Traffic Update & Dynamic Re-optimization
        TrafficUpdateRequest trafficUpdate = new TrafficUpdateRequest(
                "W1", "C1", 1.0, 2.5, System.currentTimeMillis(), "DYNAMIC_EVENT_TEST"
        );
        OptimizationResponse reoptResp = optService.reoptimize(initialId, trafficUpdate);
        String reoptId = reoptResp.getOptimizationId();
        System.out.println("Re-optimized Revision ID: " + reoptId);

        // 3. Verification of Audit History (Step 20)
        // a) Original run is preserved
        OptimizationRunEntity origRun = optService.getRunRepo().findById(initialId);
        boolean t1 = origRun != null && "COMPLETED".equals(origRun.getStatus());
        System.out.println("Test 1 (Original Optimization Run Preserved): " + (t1 ? "PASSED" : "FAILED"));

        // b) New revision run exists with parentRunId pointing to initialId
        OptimizationRunEntity revRun = optService.getRunRepo().findById(reoptId);
        boolean t2 = revRun != null && initialId.equals(revRun.getParentRunId());
        System.out.println("Test 2 (New Revision Linked to Parent Run): " + (t2 ? "PASSED" : "FAILED"));

        // c) Traffic event is persisted in database
        List<TrafficEventEntity> events = trafficService.getAllTrafficEvents();
        boolean t3 = !events.isEmpty() && "W1".equals(events.get(0).getOriginId())
                && events.get(0).getNewMultiplier() == 2.5;
        System.out.println("Test 3 (Traffic Event Persisted in Database): " + (t3 ? "PASSED" : "FAILED"));

        // d) New revision has complete persisted routes and stops
        List<FleetRouteEntity> revRoutes = optService.getRouteRepo().findByOptimizationId(reoptId);
        boolean t4 = !revRoutes.isEmpty();
        System.out.println("Test 4 (New Revision Routes Persisted): " + (t4 ? "PASSED" : "FAILED"));

        boolean allPassed = t1 && t2 && t3 && t4;

        System.out.println();
        System.out.println("========================================");
        System.out.println("DYNAMIC PERSISTENCE: " + (allPassed ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
