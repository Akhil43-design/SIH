package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DynamicReoptimizationTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("     DYNAMIC RE-OPTIMIZATION TEST");
        System.out.println("========================================");
        System.out.println();

        Location depot = new Location("W", "Central Logistics Depot");
        List<Location> depots = List.of(depot);

        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("V1", 70.0, depot, 0.12, 10.0));
        vehicles.add(new Vehicle("V2", 70.0, depot, 0.12, 10.0));

        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            DeliveryPriority p = (i % 2 == 0) ? DeliveryPriority.HIGH : DeliveryPriority.MEDIUM;
            customers.add(new Customer("C" + i, "Customer " + i, 20.0, p, 5.0, 10.0, 120.0));
        }

        RoadNetwork network = new RoadNetwork();
        List<Location> allNodes = new ArrayList<>(depots);
        allNodes.addAll(customers);

        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = 0; j < allNodes.size(); j++) {
                if (i == j) continue;
                Location f = allNodes.get(i);
                Location t = allNodes.get(j);
                double dist = 5.0 + Math.abs(i - j) * 2.0;
                network.addRoad(new Road(f, t, dist, dist * 1.5, dist * 0.10, 1));
            }
        }

        TrafficModel trafficModel = new TrafficModel();
        FleetFitnessFunction fitness = new FleetFitnessFunction();
        TrafficConfiguration config = new TrafficConfiguration(TrafficSourceMode.SIMULATED, "", null, 60000L, 5, true, 0.15);

        // 1. Initial Optimization
        MultiVehicleQIGAOptimizer initOpt = new MultiVehicleQIGAOptimizer(
                40, customers, vehicles, depots, network, trafficModel, fitness, 0.05, 0.20, 123L
        );
        FleetRoutePlan initialPlan = initOpt.optimize(50);

        System.out.println("Initial Fleet Plan:");
        for (VehicleRoute vr : initialPlan.getVehicleRoutes()) {
            System.out.print("  " + vr.getVehicle().getVehicleId() + ": " + vr.getDepot().getId());
            for (Customer c : vr.getCustomers()) {
                System.out.print(" -> " + c.getId());
            }
            System.out.println(" -> " + vr.getDepot().getId()
                    + " (TravelTime: " + String.format("%.1f", vr.getTotalTravelTime()) + " min, Cost: $"
                    + String.format("%.2f", vr.getTotalCost()) + ")");
        }
        System.out.printf("  Initial Total Travel Time: %.2f min%n", initialPlan.getTotalTravelTime());
        System.out.printf("  Initial Total Cost: $%.2f%n", initialPlan.getTotalCost());
        System.out.printf("  Initial Fitness: %.4f%n", initialPlan.getOverallFitness());
        System.out.println();

        // 2. Initialize Dynamic Optimizer & Mark Customer 1 Completed on V1
        DynamicFleetOptimizer dynamicOpt = new DynamicFleetOptimizer(
                initialPlan, depots, network, trafficModel, fitness, config, 123L
        );

        VehicleRoute v1Route = initialPlan.getVehicleRoutes().get(0);
        Customer completedCust = null;
        if (!v1Route.getCustomers().isEmpty()) {
            completedCust = v1Route.getCustomers().get(0);
            dynamicOpt.getVehicleStates().get(0).markCustomerCompleted(completedCust, 25.0);
            System.out.println("Driver Update: Vehicle 1 completed delivery to " + completedCust.getId() + " at t=25.0 min.");
        }
        System.out.println();

        // 3. Inject Traffic Surge Event on an active road segment traversed in the plan (e.g. V2's first leg)
        VehicleRoute v2Route = initialPlan.getVehicleRoutes().get(1);
        Location surgeOrigin = v2Route.getDepot();
        Location surgeDest = v2Route.getCustomers().get(0);

        TrafficUpdate trafficEvent = new TrafficUpdate(
                surgeOrigin, surgeDest, 1.0, 3.0, System.currentTimeMillis(), "SIMULATED TRAFFIC SURGE"
        );
        System.out.println("Traffic Event Injected on active road: " + trafficEvent);

        boolean reoptimized = dynamicOpt.handleTrafficUpdate(trafficEvent);
        System.out.println("Re-optimization Triggered & Accepted: " + reoptimized);
        System.out.println();

        FleetRoutePlan newPlan = dynamicOpt.getActivePlan();
        System.out.println("Re-optimized Fleet Plan:");
        for (VehicleRoute vr : newPlan.getVehicleRoutes()) {
            System.out.print("  " + vr.getVehicle().getVehicleId() + ": " + vr.getDepot().getId());
            for (Customer c : vr.getCustomers()) {
                System.out.print(" -> " + c.getId());
            }
            System.out.println(" -> " + vr.getDepot().getId()
                    + " (TravelTime: " + String.format("%.1f", vr.getTotalTravelTime()) + " min, Cost: $"
                    + String.format("%.2f", vr.getTotalCost()) + ")");
        }
        System.out.printf("  New Total Travel Time: %.2f min%n", newPlan.getTotalTravelTime());
        System.out.printf("  New Total Cost: $%.2f%n", newPlan.getTotalCost());
        System.out.printf("  New Fitness: %.4f%n", newPlan.getOverallFitness());
        System.out.println("  Re-optimization Runtime: " + dynamicOpt.getLastReoptimizationTimeMs() + " ms");
        boolean completedPreserved = completedCust == null || newPlan.getVehicleRoutes().get(0).getCustomers().get(0).equals(completedCust);
        System.out.println("  Completed Stops Preserved: " + completedPreserved);
        System.out.println("  Capacity Violations: " + (int) newPlan.getTotalCapacityViolations());
        System.out.println("  Unassigned Customers: " + newPlan.getUnassignedCount());
        System.out.println("  Duplicate Customers: " + newPlan.getDuplicateCount());

        boolean passed = reoptimized
                && completedPreserved
                && newPlan.getUnassignedCount() == 0
                && newPlan.getDuplicateCount() == 0
                && newPlan.getTotalCapacityViolations() == 0;

        System.out.println();
        System.out.println("========================================");
        System.out.println("DYNAMIC RE-OPTIMIZATION: " + (passed ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
