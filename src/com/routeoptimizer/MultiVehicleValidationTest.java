package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class MultiVehicleValidationTest {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  MULTI-VEHICLE VALIDATION SUITE");
        System.out.println("========================================");
        System.out.println();

        int passed = 0;
        int failed = 0;

        // Test 1: All customers assigned & No duplicates
        try {
            Location depot = new Location("W", "Depot");
            List<Customer> customers = createTestCustomers(6);
            List<Vehicle> vehicles = createTestVehicles(2, 100.0, depot);
            RoadNetwork network = createCompleteNetwork(depot, customers);
            TrafficModel trafficModel = new TrafficModel();
            FleetFitnessFunction fitness = new FleetFitnessFunction();

            MultiVehicleQIGAOptimizer opt = new MultiVehicleQIGAOptimizer(
                    30, customers, vehicles, depot, network, trafficModel, fitness, 0.05, 0.20, 42L
            );
            FleetRoutePlan plan = opt.optimize(50);

            if (plan.getUnassignedCount() == 0 && plan.getDuplicateCount() == 0) {
                System.out.println("[PASS] Test 1: All customers assigned and no duplicates.");
                passed++;
            } else {
                System.out.println("[FAIL] Test 1: Unassigned=" + plan.getUnassignedCount() + ", Duplicates=" + plan.getDuplicateCount());
                failed++;
            }
        } catch (Exception e) {
            System.out.println("[FAIL] Test 1: Exception - " + e.getMessage());
            failed++;
        }

        // Test 2: Capacity constraint handling
        try {
            Location depot = new Location("W", "Depot");
            List<Customer> customers = createTestCustomers(4); // Total demand = 4 * 20 = 80
            List<Vehicle> vehicles = createTestVehicles(2, 50.0, depot); // Capacity = 50 each
            RoadNetwork network = createCompleteNetwork(depot, customers);
            TrafficModel trafficModel = new TrafficModel();
            FleetFitnessFunction fitness = new FleetFitnessFunction();

            MultiVehicleQIGAOptimizer opt = new MultiVehicleQIGAOptimizer(
                    30, customers, vehicles, depot, network, trafficModel, fitness, 0.05, 0.20, 42L
            );
            FleetRoutePlan plan = opt.optimize(50);

            if (plan.getTotalCapacityViolations() == 0) {
                System.out.println("[PASS] Test 2: Capacity constraints satisfied across fleet.");
                passed++;
            } else {
                System.out.println("[FAIL] Test 2: Capacity violation = " + plan.getTotalCapacityViolations());
                failed++;
            }
        } catch (Exception e) {
            System.out.println("[FAIL] Test 2: Exception - " + e.getMessage());
            failed++;
        }

        // Test 3: Traffic impact on fitness
        try {
            Location depot = new Location("W", "Depot");
            List<Customer> customers = createTestCustomers(4);
            List<Vehicle> vehicles = createTestVehicles(2, 100.0, depot);
            RoadNetwork network = createCompleteNetwork(depot, customers);
            FleetFitnessFunction fitness = new FleetFitnessFunction();

            TrafficModel lowTraffic = new TrafficModel(TrafficCondition.LOW);
            TrafficModel highTraffic = new TrafficModel(TrafficCondition.HIGH);

            MultiVehicleQIGAOptimizer optLow = new MultiVehicleQIGAOptimizer(
                    20, customers, vehicles, depot, network, lowTraffic, fitness, 0.05, 0.20, 100L
            );
            FleetRoutePlan planLow = optLow.optimize(30);

            MultiVehicleQIGAOptimizer optHigh = new MultiVehicleQIGAOptimizer(
                    20, customers, vehicles, depot, network, highTraffic, fitness, 0.05, 0.20, 100L
            );
            FleetRoutePlan planHigh = optHigh.optimize(30);

            if (planHigh.getTotalTravelTime() > planLow.getTotalTravelTime()) {
                System.out.println("[PASS] Test 3: Traffic model correctly modulates travel time and fitness.");
                passed++;
            } else {
                System.out.println("[FAIL] Test 3: High traffic time (" + planHigh.getTotalTravelTime() + ") not greater than low traffic (" + planLow.getTotalTravelTime() + ")");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("[FAIL] Test 3: Exception - " + e.getMessage());
            failed++;
        }

        // Test 4: Priority impact on lateness penalty
        try {
            Customer lowP = new Customer("C1", "Cust 1", 10.0, DeliveryPriority.LOW, 5.0, 0.0, 10.0);
            Customer highP = new Customer("C2", "Cust 2", 10.0, DeliveryPriority.HIGH, 5.0, 0.0, 10.0);

            if (highP.getPriority().getPenaltyMultiplier() > lowP.getPriority().getPenaltyMultiplier()) {
                System.out.println("[PASS] Test 4: Priority multipliers correctly configured.");
                passed++;
            } else {
                System.out.println("[FAIL] Test 4: Priority penalty not scaled.");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("[FAIL] Test 4: Exception - " + e.getMessage());
            failed++;
        }

        // Test 5: Dynamic Customer Addition & Removal
        try {
            Location depot = new Location("W", "Depot");
            List<Customer> initialCustomers = createTestCustomers(3);
            List<Vehicle> vehicles = createTestVehicles(2, 100.0, depot);
            RoadNetwork network = createCompleteNetwork(depot, initialCustomers);

            // Add node D to network as well
            Customer extra = new Customer("D", "Customer D", 20.0, DeliveryPriority.MEDIUM, 5.0, 10.0, 100.0);
            for (Location loc : network.getRoads().stream().map(Road::getFrom).distinct().toList()) {
                network.addRoad(new Road(loc, extra, 5.0, 10.0, 0.5, 1));
                network.addRoad(new Road(extra, loc, 5.0, 10.0, 0.5, 1));
            }

            FleetCustomerManager manager = new FleetCustomerManager(
                    initialCustomers, vehicles, depot, network, new TrafficModel(), new FleetFitnessFunction()
            );

            manager.addCustomer(extra);
            if (manager.getAllCustomers().size() == 4) {
                manager.removeCustomer("D");
                if (manager.getAllCustomers().size() == 3) {
                    System.out.println("[PASS] Test 5: Dynamic customer addition and removal works.");
                    passed++;
                } else {
                    System.out.println("[FAIL] Test 5: Removal failed.");
                    failed++;
                }
            } else {
                System.out.println("[FAIL] Test 5: Addition failed.");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("[FAIL] Test 5: Exception - " + e.getMessage());
            failed++;
        }

        // Test 6: Fault handling - Empty customer list
        try {
            Location depot = new Location("W", "Depot");
            List<Vehicle> vehicles = createTestVehicles(1, 100.0, depot);
            new MultiVehicleQIGAOptimizer(
                    10, new ArrayList<>(), vehicles, depot, new RoadNetwork(), new TrafficModel(), new FleetFitnessFunction()
            );
            System.out.println("[FAIL] Test 6: Expected IllegalArgumentException for empty customers.");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Test 6: Correctly rejected empty customer list.");
            passed++;
        }

        // Test 7: Fault handling - Invalid capacity
        try {
            new Vehicle("V1", -10.0, new Location("W", "Depot"));
            System.out.println("[FAIL] Test 7: Expected IllegalArgumentException for negative capacity.");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Test 7: Correctly rejected negative capacity.");
            passed++;
        }

        // Test 8: Fault handling - Missing road
        try {
            Location depot = new Location("W", "Depot");
            List<Customer> customers = createTestCustomers(2);
            List<Vehicle> vehicles = createTestVehicles(1, 100.0, depot);
            RoadNetwork emptyNetwork = new RoadNetwork(); // no roads!

            new VehicleRoute(vehicles.get(0), customers, depot, emptyNetwork, new TrafficModel());
            System.out.println("[FAIL] Test 8: Expected IllegalArgumentException for missing road.");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Test 8: Correctly caught missing road in road network.");
            passed++;
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("  VALIDATION SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("========================================");
    }

    private static List<Customer> createTestCustomers(int count) {
        List<Customer> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(new Customer("C" + i, "Customer " + i, 20.0, DeliveryPriority.MEDIUM, 5.0, 10.0, 150.0));
        }
        return list;
    }

    private static List<Vehicle> createTestVehicles(int count, double capacity, Location depot) {
        List<Vehicle> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(new Vehicle("V" + i, capacity, depot, 0.12, 10.0));
        }
        return list;
    }

    private static RoadNetwork createCompleteNetwork(Location depot, List<Customer> customers) {
        RoadNetwork network = new RoadNetwork();
        List<Location> all = new ArrayList<>();
        all.add(depot);
        all.addAll(customers);

        for (int i = 0; i < all.size(); i++) {
            for (int j = 0; j < all.size(); j++) {
                if (i == j) continue;
                Location f = all.get(i);
                Location t = all.get(j);
                double d = 5.0 + Math.abs(i - j) * 2.0;
                network.addRoad(new Road(f, t, d, d * 1.5, d * 0.1, 1));
            }
        }
        return network;
    }
}
