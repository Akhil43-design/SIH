package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RealGeographicFleetOptimizationTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("    REAL GEOGRAPHIC QIGA FLEET TEST");
        System.out.println("========================================");
        System.out.println();

        // 1. Setup 2 Real Depots (London area)
        GeoLocation depotNorth = new GeoLocation("D1", "North London Depot", 51.5308, -0.1238);
        GeoLocation depotSouth = new GeoLocation("D2", "South London Depot", 51.5055, -0.0863);
        List<Location> depots = List.of(depotNorth, depotSouth);

        // 2. Setup 3 Vehicles (V1, V2 at D1, V3 at D2)
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("Vehicle 1 (North)", 75.0, depotNorth, 0.12, 10.0));
        vehicles.add(new Vehicle("Vehicle 2 (North)", 80.0, depotNorth, 0.11, 9.5));
        vehicles.add(new Vehicle("Vehicle 3 (South)", 90.0, depotSouth, 0.13, 10.5));

        // 3. Setup 8 Real Customers
        List<Customer> customers = new ArrayList<>();
        customers.add(new GeoCustomer("C1", "Westminster", 51.4995, -0.1332, 20.0, DeliveryPriority.HIGH, 5.0, 10.0, 60.0));
        customers.add(new GeoCustomer("C2", "Covent Garden", 51.5117, -0.1240, 15.0, DeliveryPriority.MEDIUM, 4.0, 15.0, 80.0));
        customers.add(new GeoCustomer("C3", "Canary Wharf", 51.5054, -0.0209, 30.0, DeliveryPriority.HIGH, 6.0, 20.0, 90.0));
        customers.add(new GeoCustomer("C4", "Shoreditch", 51.5245, -0.0774, 25.0, DeliveryPriority.MEDIUM, 5.0, 10.0, 75.0));
        customers.add(new GeoCustomer("C5", "Camden Town", 51.5390, -0.1426, 20.0, DeliveryPriority.LOW, 4.0, 30.0, 120.0));
        customers.add(new GeoCustomer("C6", "Southwark", 51.5033, -0.1017, 15.0, DeliveryPriority.HIGH, 5.0, 15.0, 70.0));
        customers.add(new GeoCustomer("C7", "Paddington", 51.5154, -0.1755, 35.0, DeliveryPriority.MEDIUM, 7.0, 25.0, 110.0));
        customers.add(new GeoCustomer("C8", "Greenwich", 51.4826, -0.0077, 20.0, DeliveryPriority.LOW, 5.0, 35.0, 130.0));

        // 4. Build Geographic Road Network via OSRM Provider
        List<Location> allNodes = new ArrayList<>();
        allNodes.addAll(depots);
        allNodes.addAll(customers);

        OSRMRoutingProvider routingProvider = new OSRMRoutingProvider();
        GeographicRoadNetworkBuilder networkBuilder = new GeographicRoadNetworkBuilder(routingProvider);
        RoadNetwork network = networkBuilder.buildRoadNetwork(allNodes);

        // 5. Time-Dependent Traffic Model & Multi-Objective Fleet Fitness
        TimeDependentTrafficModel trafficModel = new TimeDependentTrafficModel();
        FleetFitnessFunction fitnessFunction = new FleetFitnessFunction();

        int populationSize = 50;
        int generations = 100;
        long seed = 424242L;

        System.out.println("Depots: " + depots.size());
        System.out.println("Vehicles: " + vehicles.size());
        System.out.println("Customers: " + customers.size());
        System.out.println("Routing Provider: OSRM Real Road Network");
        System.out.println("Traffic Model: Time-Dependent Dynamic Congestion");
        System.out.println("Seed: " + seed);
        System.out.println();

        // 6. Execute MultiVehicleQIGAOptimizer
        MultiVehicleQIGAOptimizer optimizer = new MultiVehicleQIGAOptimizer(
                populationSize,
                customers,
                vehicles,
                depots,
                network,
                trafficModel,
                fitnessFunction,
                0.05,
                0.20,
                seed
        );

        FleetRoutePlan plan = optimizer.optimize(generations);

        System.out.println("========================================");
        System.out.println("      REAL GEOGRAPHIC QIGA RESULT");
        System.out.println("========================================");
        System.out.println();

        for (VehicleRoute vr : plan.getVehicleRoutes()) {
            System.out.println(vr.getVehicle().getVehicleId() + " from " + vr.getDepot().getName() + " [Demand: "
                    + vr.getTotalDemand() + "/" + vr.getVehicle().getCapacity() + "]:");
            System.out.print("  " + vr.getDepot().getId());
            for (Customer c : vr.getCustomers()) {
                System.out.print(" -> " + c.getId() + " (" + c.getName() + ")");
            }
            System.out.println(" -> " + vr.getDepot().getId());
            System.out.printf("  Road Distance: %.2f km | Travel Time: %.2f min | Fuel: %.2f L | Cost: $%.2f%n",
                    vr.getTotalDistance(), vr.getTotalTravelTime(), vr.getTotalFuel(), vr.getTotalCost());
            System.out.println();
        }

        System.out.println("----------------------------------------");
        System.out.printf("Total Road Distance: %.2f km%n", plan.getTotalDistance());
        System.out.printf("Total Travel Time: %.2f min%n", plan.getTotalTravelTime());
        System.out.printf("Total Fuel: %.2f L%n", plan.getTotalFuel());
        System.out.printf("Total Cost: $%.2f%n", plan.getTotalCost());
        System.out.printf("Optimization Score: %.4f%n", plan.getOverallFitness());
        System.out.println();
        System.out.println("Capacity Violations: " + (int) plan.getTotalCapacityViolations());
        System.out.println("Time Violations: " + plan.getTotalTimeViolations());
        System.out.println("Unassigned Customers: " + plan.getUnassignedCount());
        System.out.println("Duplicate Customers: " + plan.getDuplicateCount());
        System.out.println("QIGA Optimization Runtime: " + optimizer.getOptimizationRuntimeMs() + " ms");
        System.out.println("Routing Provider: OSRM");
        System.out.println("Traffic Model: Simulated Time-Dependent");
        System.out.println("========================================");
    }
}
