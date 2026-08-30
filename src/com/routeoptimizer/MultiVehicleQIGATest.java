package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MultiVehicleQIGATest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("    MULTI-VEHICLE QIGA FLEET TEST");
        System.out.println("========================================");
        System.out.println();

        // 1. Setup Depot
        Location depot = new Location("Depot", "Central Hub");

        // 2. Setup 10 Customers with Demand, Time Windows, Service Times, and Priorities
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("A", "Customer A", 25.0, DeliveryPriority.HIGH, 5.0, 10.0, 60.0));
        customers.add(new Customer("B", "Customer B", 15.0, DeliveryPriority.MEDIUM, 4.0, 15.0, 80.0));
        customers.add(new Customer("C", "Customer C", 30.0, DeliveryPriority.HIGH, 6.0, 20.0, 90.0));
        customers.add(new Customer("D", "Customer D", 20.0, DeliveryPriority.LOW, 5.0, 30.0, 120.0));
        customers.add(new Customer("E", "Customer E", 35.0, DeliveryPriority.HIGH, 7.0, 10.0, 70.0));
        customers.add(new Customer("F", "Customer F", 10.0, DeliveryPriority.MEDIUM, 3.0, 25.0, 100.0));
        customers.add(new Customer("G", "Customer G", 20.0, DeliveryPriority.LOW, 5.0, 40.0, 140.0));
        customers.add(new Customer("H", "Customer H", 25.0, DeliveryPriority.MEDIUM, 5.0, 20.0, 110.0));
        customers.add(new Customer("I", "Customer I", 15.0, DeliveryPriority.HIGH, 4.0, 15.0, 75.0));
        customers.add(new Customer("J", "Customer J", 30.0, DeliveryPriority.MEDIUM, 6.0, 35.0, 130.0));

        // 3. Setup 3 Vehicles with capacities
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("Vehicle 1", 90.0, depot, 0.12, 10.0));
        vehicles.add(new Vehicle("Vehicle 2", 80.0, depot, 0.11, 9.5));
        vehicles.add(new Vehicle("Vehicle 3", 100.0, depot, 0.14, 11.0));

        // 4. Build Complete Road Network
        RoadNetwork network = new RoadNetwork();
        List<Location> allNodes = new ArrayList<>();
        allNodes.add(depot);
        allNodes.addAll(customers);

        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = 0; j < allNodes.size(); j++) {
                if (i == j) continue;
                Location from = allNodes.get(i);
                Location to = allNodes.get(j);

                double dist = 5.0 + Math.abs(i - j) * 2.5 + ((i * 3 + j * 2) % 4);
                double time = dist * 1.5;
                double fuel = dist * 0.10;
                int traffic = 1 + ((i + j) % 3);

                network.addRoad(new Road(from, to, dist, time, fuel, traffic));
            }
        }

        // 5. Traffic Model (MEDIUM Traffic)
        TrafficModel trafficModel = new TrafficModel(TrafficCondition.MEDIUM);

        // 6. Fleet Fitness Function
        FleetFitnessFunction fitnessFunction = new FleetFitnessFunction(
                0.25, 0.25, 0.20, 0.30,
                300.0, 600.0, 50.0, 3000.0,
                100.0, 20.0
        );

        int populationSize = 50;
        int generations = 100;
        long seed = 12345L;

        System.out.println("Customers: " + customers.size());
        System.out.println("Vehicles: " + vehicles.size());
        System.out.println("Population: " + populationSize);
        System.out.println("Generations: " + generations);
        System.out.println("Traffic Condition: " + trafficModel.getDefaultCondition() + " (" + trafficModel.getDefaultCondition().getMultiplier() + "x)");
        System.out.println("Seed: " + seed);
        System.out.println();

        // 7. Run Multi-Vehicle QIGA Optimizer
        MultiVehicleQIGAOptimizer optimizer = new MultiVehicleQIGAOptimizer(
                populationSize,
                customers,
                vehicles,
                depot,
                network,
                trafficModel,
                fitnessFunction,
                0.05,
                0.20,
                seed
        );

        FleetRoutePlan plan = optimizer.optimize(generations);

        // 8. Output Results
        System.out.println("========================================");
        System.out.println("       MULTI-VEHICLE QIGA RESULT");
        System.out.println("========================================");
        System.out.println();

        for (int i = 0; i < plan.getVehicleRoutes().size(); i++) {
            VehicleRoute vr = plan.getVehicleRoutes().get(i);
            System.out.println(vr.getVehicle().getVehicleId() + " (Capacity: " + vr.getVehicle().getCapacity() + ", Demand: " + vr.getTotalDemand() + "):");
            System.out.print("  " + depot.getId());
            for (Customer c : vr.getCustomers()) {
                System.out.print(" -> " + c.getId() + " [D:" + c.getDemand() + ", P:" + c.getPriority() + "]");
            }
            System.out.println(" -> " + depot.getId());
            System.out.printf("  Distance: %.2f km | TravelTime: %.2f min | Fuel: %.2f L | Cost: $%.2f%n",
                    vr.getTotalDistance(), vr.getTotalTravelTime(), vr.getTotalFuel(), vr.getTotalCost());
            System.out.println();
        }

        System.out.println("----------------------------------------");
        System.out.printf("Total Distance: %.2f km%n", plan.getTotalDistance());
        System.out.printf("Total Travel Time: %.2f min%n", plan.getTotalTravelTime());
        System.out.printf("Total Fuel: %.2f L%n", plan.getTotalFuel());
        System.out.printf("Total Cost: $%.2f%n", plan.getTotalCost());
        System.out.printf("Optimization Score (Fitness): %.4f%n", plan.getOverallFitness());
        System.out.println();
        System.out.println("Capacity Violations: " + (int) plan.getTotalCapacityViolations());
        System.out.println("Time Violations: " + plan.getTotalTimeViolations());
        System.out.println("Unassigned Customers: " + plan.getUnassignedCount());
        System.out.println("Duplicate Customers: " + plan.getDuplicateCount());
        System.out.println("Optimization Runtime: " + optimizer.getOptimizationRuntimeMs() + " ms");
        System.out.println("Local Search Runtime: " + optimizer.getLocalSearchRuntimeMs() + " ms");
        System.out.println("Solutions Improved: " + optimizer.getSolutionsImprovedCount());
        System.out.println("First Best Gen: " + optimizer.getFirstBestGeneration());
        System.out.println("Last Improvement Gen: " + optimizer.getLastImprovementGeneration());
        System.out.println("========================================");
        System.out.println();

        // 9. Run Reproducibility Test
        System.out.println("----------------------------------------");
        System.out.println("RUNNING REPRODUCIBILITY TEST WITH SEED " + seed);
        MultiVehicleQIGAOptimizer opt2 = new MultiVehicleQIGAOptimizer(
                populationSize,
                customers,
                vehicles,
                depot,
                network,
                trafficModel,
                fitnessFunction,
                0.05,
                0.20,
                seed
        );
        FleetRoutePlan plan2 = opt2.optimize(generations);
        boolean reproducible = Math.abs(plan.getOverallFitness() - plan2.getOverallFitness()) < 1e-9;
        System.out.println("Reproducibility Result: " + (reproducible ? "PASSED" : "FAILED"));
        System.out.println("----------------------------------------");
    }
}
