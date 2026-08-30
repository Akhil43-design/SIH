package com.routeoptimizer;

import java.io.File;
import java.util.Locale;

public class PersistenceRestartTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("       PERSISTENCE RESTART TEST");
        System.out.println("========================================");
        System.out.println();

        String testDbFile = "target_test_restart_db.dat";
        new File(testDbFile).delete();

        DatabaseConfiguration config = new DatabaseConfiguration(
                DatabaseConfiguration.DatabaseType.EMBEDDED_PERSISTENT, testDbFile
        );

        String savedOptId;
        double savedDistance;
        double savedScore;

        // Session 1: Run optimization and save to file database
        {
            DatabaseManager db1 = new DatabaseManager(config);
            FleetManagementService fleetService1 = new FleetManagementService(db1);
            TrafficService trafficService1 = new TrafficService(db1);
            OptimizationService optService1 = new OptimizationService(fleetService1, trafficService1, db1);

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

            OptimizationResponse resp1 = optService1.runOptimization(req);
            savedOptId = resp1.getOptimizationId();
            savedDistance = resp1.getTotalDistanceKm();
            savedScore = resp1.getOptimizationScore();

            System.out.println("Session 1: Created & Persisted Optimization ID: " + savedOptId);
            System.out.printf("Session 1: Total Distance: %.2f km, Score: %.4f%n", savedDistance, savedScore);
        }

        // Session 2: Fresh instance (simulating application crash/restart)
        {
            System.out.println();
            System.out.println("--- Simulating Server Restart & Reloading Database from Disk ---");
            DatabaseManager db2 = new DatabaseManager(config); // Reads from disk
            FleetManagementService fleetService2 = new FleetManagementService(db2);
            TrafficService trafficService2 = new TrafficService(db2);
            OptimizationService optService2 = new OptimizationService(fleetService2, trafficService2, db2);

            // In-memory sessions are completely empty
            boolean sessionEmpty = optService2.getSessions().isEmpty();
            System.out.println("Test 1 (In-Memory Session Cache Empty): " + (sessionEmpty ? "PASSED" : "FAILED"));

            // Retrieve result by ID from Database
            OptimizationResponse resp2 = optService2.getOptimization(savedOptId);

            boolean t2 = resp2 != null && "COMPLETED".equals(resp2.getStatus());
            System.out.println("Test 2 (Recovered Optimization from Database): " + (t2 ? "PASSED" : "FAILED"));

            boolean t3 = resp2 != null && Math.abs(resp2.getTotalDistanceKm() - savedDistance) < 1e-4;
            System.out.println("Test 3 (Recovered Exact Total Distance): " + (t3 ? "PASSED" : "FAILED"));

            boolean t4 = resp2 != null && Math.abs(resp2.getOptimizationScore() - savedScore) < 1e-4;
            System.out.println("Test 4 (Recovered Exact Optimization Score): " + (t4 ? "PASSED" : "FAILED"));

            boolean t5 = resp2 != null && resp2.getVehicleRoutes().size() == 2;
            System.out.println("Test 5 (Recovered Complete Vehicle Routes): " + (t5 ? "PASSED" : "FAILED"));

            boolean allPassed = sessionEmpty && t2 && t3 && t4 && t5;

            System.out.println();
            System.out.println("========================================");
            System.out.println("PERSISTENCE RESTART: " + (allPassed ? "PASSED" : "FAILED"));
            System.out.println("========================================");

            // Clean up test file
            new File(testDbFile).delete();
        }
    }
}
