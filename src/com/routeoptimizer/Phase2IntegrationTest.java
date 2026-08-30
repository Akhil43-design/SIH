package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Phase2IntegrationTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("      PHASE 2 INTEGRATION TEST");
        System.out.println("========================================");
        System.out.println();

        // 1. Setup 2 Depots
        Location depot1 = new Location("W1", "West Logistics Depot");
        Location depot2 = new Location("W2", "East Logistics Depot");
        List<Location> depots = List.of(depot1, depot2);

        // 2. Setup 4 Vehicles (2 per depot)
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("V1-W1", 80.0, depot1, 0.12, 10.0));
        vehicles.add(new Vehicle("V2-W1", 90.0, depot1, 0.11, 9.5));
        vehicles.add(new Vehicle("V3-W2", 85.0, depot2, 0.12, 10.0));
        vehicles.add(new Vehicle("V4-W2", 95.0, depot2, 0.13, 10.5));

        // 3. Setup 12 Customers with priorities, demands, and time windows
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            DeliveryPriority p = (i % 3 == 0) ? DeliveryPriority.HIGH : (i % 3 == 1 ? DeliveryPriority.MEDIUM : DeliveryPriority.LOW);
            double demand = 15.0 + (i % 5) * 5.0;
            double service = 5.0;
            double earliest = 300.0 + (i % 4) * 30.0; // Morning shift
            double latest = earliest + 90.0;
            customers.add(new Customer("C" + i, "Customer " + i, demand, p, service, earliest, latest));
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
                double d = 6.0 + Math.abs(i - j) * 2.5;
                network.addRoad(new Road(f, t, d, d * 1.5, d * 0.10, 1 + (i + j) % 3));
            }
        }

        // 5. Time-Dependent Traffic Model
        TimeDependentTrafficModel trafficModel = new TimeDependentTrafficModel();

        // 6. Fleet Fitness Function
        FleetFitnessFunction fitness = new FleetFitnessFunction();

        // 7. Run QIGA
        System.out.println("Running Phase 2 Multi-Depot Time-Dependent QIGA...");
        MultiVehicleQIGAOptimizer qiga = new MultiVehicleQIGAOptimizer(
                50, customers, vehicles, depots, network, trafficModel, fitness, 0.05, 0.20, 777L
        );
        FleetRoutePlan qigaPlan = qiga.optimize(100);

        System.out.println();
        System.out.println("QIGA Solution:");
        for (VehicleRoute vr : qigaPlan.getVehicleRoutes()) {
            System.out.println(vr.getVehicle().getVehicleId() + " [Depot: " + vr.getDepot().getId() + ", Demand: " + vr.getTotalDemand() + "]:");
            System.out.print("  " + vr.getDepot().getId());
            for (Customer c : vr.getCustomers()) {
                System.out.print(" -> " + c.getId());
            }
            System.out.println(" -> " + vr.getDepot().getId());
            System.out.printf("  TravelTime: %.2f min | Waiting: %.2f min | Lateness: %.2f | Cost: $%.2f%n",
                    vr.getTotalTravelTime(), vr.getTotalWaitingTime(), vr.getTotalLateness(), vr.getTotalCost());
        }
        System.out.printf("QIGA Overall Fitness: %.4f (Runtime: %d ms)%n", qigaPlan.getOverallFitness(), qiga.getOptimizationRuntimeMs());

        // 8. Run Classical GA
        System.out.println();
        System.out.println("Running Classical GA Baseline...");
        ClassicalGAOptimizer ga = new ClassicalGAOptimizer(
                50, customers, vehicles, depot1, network, trafficModel, fitness, 777L
        );
        FleetRoutePlan gaPlan = ga.optimize(100);
        System.out.printf("Classical GA Overall Fitness: %.4f (Runtime: %d ms)%n", gaPlan.getOverallFitness(), ga.getOptimizationRuntimeMs());

        // Validations
        boolean qigaValid = qigaPlan.getUnassignedCount() == 0
                && qigaPlan.getDuplicateCount() == 0
                && qigaPlan.getTotalCapacityViolations() == 0;

        System.out.println();
        System.out.println("========================================");
        System.out.println("PHASE 2 INTEGRATION: " + (qigaValid ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
