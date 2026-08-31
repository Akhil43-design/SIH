package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class LargeScaleOptimizationTest {

    public static void main(String[] args) {
        System.out.println("Running LargeScaleOptimizationTest...");
        int numCustomers = 1000;
        List<Customer> dataset = LargeScaleDatasetGenerator.generateDeterministicDataset(numCustomers, "bengaluru", 42L);
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            vehicles.add(new Vehicle("V" + i, 1000.0, new Location("DEPOT", "Central Hub")));
        }
        
        HierarchicalFleetOptimizer optimizer = new HierarchicalFleetOptimizer(
                100, // max cluster size
                4,   // max concurrent threads
                true, // deterministic mode
                42L,  // seed
                10,   // pop size (small for fast test)
                10    // generations
        );
        
        RoadNetwork mockNetwork = new RoadNetwork();
        TrafficModel mockTraffic = new TrafficModel();
        FleetFitnessFunction fitness = new FleetFitnessFunction();
        
        long beforeMemory = MemoryProfiler.getUsedMemoryMb();
        long start = System.currentTimeMillis();
        
        FleetRoutePlan finalPlan = optimizer.optimize(
                dataset, vehicles, vehicles.get(0).getCurrentLocation(), 
                mockNetwork, mockTraffic, fitness, "bengaluru"
        );
        
        long end = System.currentTimeMillis();
        long afterMemory = MemoryProfiler.getUsedMemoryMb();
        
        optimizer.shutdown();
        
        if (finalPlan == null) {
            throw new AssertionError("Final plan should not be null");
        }
        
        if (finalPlan.getVehicleRoutes() == null) {
            throw new AssertionError("Vehicle routes should not be null");
        }
        
        int totalRoutedCustomers = 0;
        for (VehicleRoute vr : finalPlan.getVehicleRoutes()) {
            totalRoutedCustomers += vr.getCustomers().size();
        }
        
        System.out.println("Hierarchical Optimization 1000 customers:");
        System.out.println("Runtime: " + (end - start) + " ms");
        System.out.println("Peak Memory delta: " + Math.max(0, afterMemory - beforeMemory) + " MB");
        System.out.println("Total Clusters processed: " + optimizer.getTotalClusters());
        
        if (numCustomers != totalRoutedCustomers) {
            throw new AssertionError("Hierarchical optimizer dropped customers. Expected " + numCustomers + ", got " + totalRoutedCustomers);
        }
        
        System.out.println("LargeScaleOptimizationTest: PASS");
    }
}
