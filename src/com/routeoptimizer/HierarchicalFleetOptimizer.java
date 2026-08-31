package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HierarchicalFleetOptimizer {

    private final LargeScaleCustomerPartitioner partitioner;
    private final ParallelClusterOptimizer parallelOptimizer;
    private final int maxClusterSize;
    private final boolean deterministicMode;
    private final Long seed;
    
    // Config values
    private final int popSize;
    private final int generations;
    
    private int totalClusters = 0;
    private long totalRuntimeMs = 0;

    public HierarchicalFleetOptimizer(
            int maxClusterSize,
            int maxConcurrentThreads,
            boolean deterministicMode,
            Long seed,
            int popSize,
            int generations) {
        
        this.partitioner = new LargeScaleCustomerPartitioner();
        this.parallelOptimizer = new ParallelClusterOptimizer(maxConcurrentThreads, deterministicMode);
        this.maxClusterSize = maxClusterSize;
        this.deterministicMode = deterministicMode;
        this.seed = seed;
        this.popSize = popSize;
        this.generations = generations;
    }

    public FleetRoutePlan optimize(
            List<Customer> allCustomers,
            List<Vehicle> allVehicles,
            Location centralDepot,
            RoadNetwork network,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction,
            String city) {

        long startTime = System.currentTimeMillis();

        // 1. Partition
        List<CustomerCluster> clusters = partitioner.partition(allCustomers, maxClusterSize, city);
        this.totalClusters = clusters.size();

        // 2. Assign Vehicles to Clusters (simplified approximation)
        // In reality, this requires a knapsack or capacity balancing step.
        // We will evenly distribute vehicles for now.
        if (clusters.isEmpty() || allVehicles.isEmpty()) {
            return new FleetRoutePlan(centralDepot, new ArrayList<>(), allCustomers, fitnessFunction);
        }
        
        int vehiclesPerCluster = Math.max(1, allVehicles.size() / clusters.size());
        int vehicleIndex = 0;
        for (CustomerCluster cluster : clusters) {
            int toAssign = Math.min(vehiclesPerCluster, allVehicles.size() - vehicleIndex);
            if (toAssign == 0 && vehicleIndex < allVehicles.size()) toAssign = 1;
            
            for (int i = 0; i < toAssign && vehicleIndex < allVehicles.size(); i++) {
                cluster.assignVehicle(allVehicles.get(vehicleIndex++));
            }
        }
        
        // Ensure every cluster has at least one vehicle (borrow from others if needed)
        for (CustomerCluster cluster : clusters) {
            if (cluster.getAssignedVehicles().isEmpty()) {
                cluster.assignVehicle(allVehicles.get(0));
            }
        }

        // 3. Prepare Sub-problems
        List<Callable<FleetRoutePlan>> tasks = new ArrayList<>();
        
        for (int i = 0; i < clusters.size(); i++) {
            final CustomerCluster cluster = clusters.get(i);
            final Long clusterSeed = deterministicMode ? (seed != null ? seed + i : (long) i) : null;
            
            tasks.add(() -> {
                MultiVehicleQIGAOptimizer subOptimizer = new MultiVehicleQIGAOptimizer(
                        popSize,
                        cluster.getCustomers(),
                        cluster.getAssignedVehicles(),
                        centralDepot,
                        network,
                        trafficModel,
                        fitnessFunction,
                        0.05,
                        0.20,
                        clusterSeed
                );
                return subOptimizer.optimize(generations);
            });
        }

        // 4. Execute in Parallel
        List<FleetRoutePlan> subPlans = parallelOptimizer.optimizeClusters(tasks);
        
        // 5. Fleet Route Assembly
        List<VehicleRoute> finalRoutes = new ArrayList<>();
        for (FleetRoutePlan subPlan : subPlans) {
            if (subPlan != null && subPlan.getVehicleRoutes() != null) {
                // Filter out empty routes to avoid depot-only trips
                for (VehicleRoute vr : subPlan.getVehicleRoutes()) {
                    if (!vr.getCustomers().isEmpty()) {
                        finalRoutes.add(vr);
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        this.totalRuntimeMs = endTime - startTime;

        return new FleetRoutePlan(centralDepot, finalRoutes, allCustomers, fitnessFunction);
    }
    
    public int getTotalClusters() {
        return totalClusters;
    }
    
    public long getTotalRuntimeMs() {
        return totalRuntimeMs;
    }

    public void shutdown() {
        parallelOptimizer.shutdown();
    }
}
