package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MultiDepotValidationTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("     MULTI-DEPOT VALIDATION TEST");
        System.out.println("========================================");
        System.out.println();

        // 1. Setup 3 Depots
        Location depot1 = new Location("W1", "North Depot");
        Location depot2 = new Location("W2", "East Depot");
        Location depot3 = new Location("W3", "South Depot");
        List<Location> depots = List.of(depot1, depot2, depot3);

        // 2. Setup 3 Vehicles, each assigned to a different home depot
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("V1 (W1)", 90.0, depot1, 0.12, 10.0));
        vehicles.add(new Vehicle("V2 (W2)", 85.0, depot2, 0.11, 9.5));
        vehicles.add(new Vehicle("V3 (W3)", 95.0, depot3, 0.13, 10.5));

        // 3. Setup 12 Customers
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            DeliveryPriority p = (i % 3 == 0) ? DeliveryPriority.HIGH : (i % 3 == 1 ? DeliveryPriority.MEDIUM : DeliveryPriority.LOW);
            double demand = 15.0 + (i % 4) * 5.0;
            customers.add(new Customer("C" + i, "Customer " + i, demand, p, 5.0, 10.0, 150.0));
        }

        // 4. Complete Road Network
        RoadNetwork network = new RoadNetwork();
        List<Location> allNodes = new ArrayList<>(depots);
        allNodes.addAll(customers);

        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = 0; j < allNodes.size(); j++) {
                if (i == j) continue;
                Location f = allNodes.get(i);
                Location t = allNodes.get(j);
                double d = 4.0 + Math.abs(i - j) * 2.0;
                network.addRoad(new Road(f, t, d, d * 1.4, d * 0.09, 1));
            }
        }

        TrafficModel trafficModel = new TrafficModel();
        FleetFitnessFunction fitnessFunction = new FleetFitnessFunction();

        // 5. Optimize with MultiVehicleQIGAOptimizer
        MultiVehicleQIGAOptimizer optimizer = new MultiVehicleQIGAOptimizer(
                50, customers, vehicles, depots, network, trafficModel, fitnessFunction, 0.05, 0.20, 9999L
        );

        FleetRoutePlan plan = optimizer.optimize(100);

        System.out.println("Multi-Depot Fleet Routes:");
        for (VehicleRoute vr : plan.getVehicleRoutes()) {
            System.out.println(vr.getVehicle().getVehicleId() + " from Home Depot " + vr.getDepot().getId() + ":");
            System.out.print("  " + vr.getDepot().getId());
            for (Customer c : vr.getCustomers()) {
                System.out.print(" -> " + c.getId());
            }
            System.out.println(" -> " + vr.getDepot().getId());
            System.out.printf("  Demand: %.1f/%.1f | Dist: %.2f km | Cost: $%.2f%n",
                    vr.getTotalDemand(), vr.getVehicle().getCapacity(), vr.getTotalDistance(), vr.getTotalCost());
        }
        System.out.println();
        System.out.printf("Total Fleet Distance: %.2f km%n", plan.getTotalDistance());
        System.out.printf("Total Fleet Cost: $%.2f%n", plan.getTotalCost());
        System.out.printf("Overall Fitness: %.4f%n", plan.getOverallFitness());
        System.out.println("Capacity Violations: " + (int) plan.getTotalCapacityViolations());
        System.out.println("Unassigned Customers: " + plan.getUnassignedCount());
        System.out.println("Duplicate Customers: " + plan.getDuplicateCount());

        boolean passed = plan.getUnassignedCount() == 0
                && plan.getDuplicateCount() == 0
                && plan.getTotalCapacityViolations() == 0;

        System.out.println();
        System.out.println("========================================");
        System.out.println("MULTI-DEPOT VALIDATION: " + (passed ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
