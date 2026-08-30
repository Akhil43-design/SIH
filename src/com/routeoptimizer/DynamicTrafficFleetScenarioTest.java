package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DynamicTrafficFleetScenarioTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println(" DYNAMIC TRAFFIC FLEET SCENARIO TEST");
        System.out.println("========================================");
        System.out.println();

        // 1. Setup 2 Depots
        Location depot1 = new Location("W1", "North Logistic Hub");
        Location depot2 = new Location("W2", "South Logistic Hub");
        List<Location> depots = List.of(depot1, depot2);

        // 2. Setup 3 Vehicles (2 at W1, 1 at W2)
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("V1 (W1)", 75.0, depot1, 0.12, 10.0));
        vehicles.add(new Vehicle("V2 (W1)", 75.0, depot1, 0.12, 10.0));
        vehicles.add(new Vehicle("V3 (W2)", 85.0, depot2, 0.12, 10.0));

        // 3. Setup 10 Customers
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            DeliveryPriority p = (i % 3 == 0) ? DeliveryPriority.HIGH : (i % 3 == 1 ? DeliveryPriority.MEDIUM : DeliveryPriority.LOW);
            customers.add(new Customer("C" + i, "Customer " + i, 15.0 + (i % 3) * 5.0, p, 5.0, 10.0, 150.0));
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
                network.addRoad(new Road(f, t, d, d * 1.5, d * 0.10, 1));
            }
        }

        TrafficModel trafficModel = new TrafficModel();
        FleetFitnessFunction fitness = new FleetFitnessFunction();
        TrafficConfiguration config = new TrafficConfiguration();

        // 5. Initial Fleet Optimization
        System.out.println("Phase 1: Initial Multi-Depot Fleet Optimization...");
        MultiVehicleQIGAOptimizer initOpt = new MultiVehicleQIGAOptimizer(
                50, customers, vehicles, depots, network, trafficModel, fitness, 0.05, 0.20, 999L
        );
        FleetRoutePlan initialPlan = initOpt.optimize(100);

        System.out.printf("  Initial Distance: %.2f km | Time: %.2f min | Cost: $%.2f | Score: %.4f%n",
                initialPlan.getTotalDistance(), initialPlan.getTotalTravelTime(),
                initialPlan.getTotalCost(), initialPlan.getOverallFitness());
        System.out.println();

        // 6. Dynamic Engine Initialization
        DynamicFleetOptimizer dynamicEngine = new DynamicFleetOptimizer(
                initialPlan, depots, network, trafficModel, fitness, config, 999L
        );

        // 7. Inject Sudden Heavy Congestion Event (3.0x on a key arterial corridor)
        Location congestedOrigin = depot1;
        Location congestedDest = customers.get(0);
        TrafficUpdate congestionEvent = new TrafficUpdate(
                congestedOrigin, congestedDest, 1.0, 3.0, System.currentTimeMillis(), "SIMULATED ARTERIAL GRIDLOCK"
        );

        System.out.println("Phase 2: Sudden Traffic Congestion Event Injected:");
        System.out.println("  " + congestionEvent);
        System.out.println();

        System.out.println("Phase 3: Dynamic Fleet Re-Optimization Triggered...");
        boolean reoptSuccess = dynamicEngine.handleTrafficUpdate(congestionEvent);
        System.out.println("  Re-optimization Status: " + (reoptSuccess ? "ACCEPTED NEW OPTIMAL PLAN" : "RETAINED PLAN"));

        FleetRoutePlan finalPlan = dynamicEngine.getActivePlan();
        System.out.println();
        System.out.println("========================================");
        System.out.println("      SCENARIO METRIC COMPARISON");
        System.out.println("========================================");
        System.out.println();

        System.out.println("BEFORE TRAFFIC EVENT:");
        System.out.printf("  Distance: %.2f km%n", initialPlan.getTotalDistance());
        System.out.printf("  Travel Time: %.2f min%n", initialPlan.getTotalTravelTime());
        System.out.printf("  Fuel: %.2f L%n", initialPlan.getTotalFuel());
        System.out.printf("  Cost: $%.2f%n", initialPlan.getTotalCost());
        System.out.printf("  Score: %.4f%n", initialPlan.getOverallFitness());
        System.out.println();

        System.out.println("AFTER CONGESTION (PRE RE-OPT):");
        System.out.printf("  Congested Score: %.4f%n", dynamicEngine.getLastPreReoptFitness());
        System.out.println();

        System.out.println("AFTER DYNAMIC RE-OPTIMIZATION:");
        System.out.printf("  Distance: %.2f km%n", finalPlan.getTotalDistance());
        System.out.printf("  Travel Time: %.2f min%n", finalPlan.getTotalTravelTime());
        System.out.printf("  Fuel: %.2f L%n", finalPlan.getTotalFuel());
        System.out.printf("  Cost: $%.2f%n", finalPlan.getTotalCost());
        System.out.printf("  Score: %.4f%n", finalPlan.getOverallFitness());
        System.out.println("  Re-optimization Runtime: " + dynamicEngine.getLastReoptimizationTimeMs() + " ms");
        System.out.println();

        System.out.println("Route Verification:");
        System.out.println("  Capacity Violations: " + (int) finalPlan.getTotalCapacityViolations());
        System.out.println("  Unassigned Customers: " + finalPlan.getUnassignedCount());
        System.out.println("  Duplicate Customers: " + finalPlan.getDuplicateCount());

        boolean passed = finalPlan.getUnassignedCount() == 0
                && finalPlan.getDuplicateCount() == 0
                && finalPlan.getTotalCapacityViolations() == 0;

        System.out.println();
        System.out.println("========================================");
        System.out.println("DYNAMIC SCENARIO TEST: " + (passed ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
