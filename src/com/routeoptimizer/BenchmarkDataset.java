package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkDataset {

    private final String name;
    private final int vehicleCount;
    private final double vehicleCapacity;
    private final List<Location> depots;
    private final List<Customer> customers;
    private final RoadNetwork roadNetwork;

    public BenchmarkDataset(
            String name,
            int vehicleCount,
            double vehicleCapacity,
            List<Location> depots,
            List<Customer> customers,
            RoadNetwork roadNetwork) {

        this.name = name;
        this.vehicleCount = vehicleCount;
        this.vehicleCapacity = vehicleCapacity;
        this.depots = new ArrayList<>(depots);
        this.customers = new ArrayList<>(customers);
        this.roadNetwork = roadNetwork;
    }

    public static BenchmarkDataset createSyntheticBenchmark(
            String name,
            int numDepots,
            int numVehicles,
            double capacity,
            int numCustomers) {

        List<Location> depots = new ArrayList<>();
        for (int i = 1; i <= numDepots; i++) {
            depots.add(new Location("W" + i, "Depot " + i));
        }

        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= numCustomers; i++) {
            DeliveryPriority p = (i % 3 == 0) ? DeliveryPriority.HIGH : (i % 3 == 1 ? DeliveryPriority.MEDIUM : DeliveryPriority.LOW);
            double demand = 10.0 + (i * 3) % 25;
            double service = 5.0;
            double earliest = 10.0 + (i % 5) * 10.0;
            double latest = earliest + 50.0 + (i % 4) * 15.0;
            customers.add(new Customer("C" + i, "Customer " + i, demand, p, service, earliest, latest));
        }

        RoadNetwork network = new RoadNetwork();
        List<Location> allNodes = new ArrayList<>();
        allNodes.addAll(depots);
        allNodes.addAll(customers);

        for (int i = 0; i < allNodes.size(); i++) {
            for (int j = 0; j < allNodes.size(); j++) {
                if (i == j) continue;
                Location f = allNodes.get(i);
                Location t = allNodes.get(j);
                double dist = 4.0 + Math.abs(i - j) * 2.0 + ((i * 2 + j * 3) % 4);
                double time = dist * 1.5;
                double fuel = dist * 0.10;
                int traffic = 1 + ((i + j) % 3);
                network.addRoad(new Road(f, t, dist, time, fuel, traffic));
            }
        }

        return new BenchmarkDataset(name, numVehicles, capacity, depots, customers, network);
    }

    public String getName() {
        return name;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public double getVehicleCapacity() {
        return vehicleCapacity;
    }

    public List<Location> getDepots() {
        return depots;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public RoadNetwork getRoadNetwork() {
        return roadNetwork;
    }

    public List<Vehicle> buildVehicles() {
        List<Vehicle> list = new ArrayList<>();
        for (int i = 0; i < vehicleCount; i++) {
            Location d = depots.get(i % depots.size());
            list.add(new Vehicle("V" + (i + 1), vehicleCapacity, d, 0.12, 10.0));
        }
        return list;
    }
}
