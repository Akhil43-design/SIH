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
        
        // 5. Fleet Route Assembly & Boundary Optimization
        List<VehicleRoute> finalRoutes = new ArrayList<>();
        for (FleetRoutePlan subPlan : subPlans) {
            if (subPlan != null && subPlan.getVehicleRoutes() != null) {
                for (VehicleRoute vr : subPlan.getVehicleRoutes()) {
                    if (!vr.getCustomers().isEmpty()) {
                        finalRoutes.add(vr);
                    }
                }
            }
        }
        
        performBoundaryOptimization(finalRoutes, network);

        long endTime = System.currentTimeMillis();
        this.totalRuntimeMs = endTime - startTime;

        return new FleetRoutePlan(centralDepot, finalRoutes, allCustomers, fitnessFunction);
    }
    
    private void performBoundaryOptimization(List<VehicleRoute> routes, RoadNetwork network) {
        if (routes.size() < 2) return;
        
        boolean improved = true;
        int iterations = 0;
        int maxIterations = 50; // bounded
        
        while (improved && iterations < maxIterations) {
            improved = false;
            iterations++;
            
            for (int i = 0; i < routes.size(); i++) {
                VehicleRoute r1 = routes.get(i);
                if (r1.getCustomers().isEmpty()) continue;
                
                for (int j = i + 1; j < routes.size(); j++) {
                    VehicleRoute r2 = routes.get(j);
                    if (r2.getCustomers().isEmpty()) continue;
                    
                    // Simple boundary heuristic: 
                    // Try to move the last customer of r1 to the beginning of r2
                    Customer c = r1.getCustomers().get(r1.getCustomers().size() - 1);
                    
                    // Check capacity constraints
                    double currentDemand = r2.getCustomers().stream().mapToDouble(Customer::getDemand).sum();
                    if (currentDemand + c.getDemand() <= r2.getVehicle().getCapacity()) {
                        
                        // Evaluate spatial distance
                        double distR1Old = calculateRouteDistance(r1, network);
                        double distR2Old = calculateRouteDistance(r2, network);
                        
                        r1.getCustomers().remove(r1.getCustomers().size() - 1);
                        r2.getCustomers().add(0, c);
                        
                        double distR1New = calculateRouteDistance(r1, network);
                        double distR2New = calculateRouteDistance(r2, network);
                        
                        if ((distR1New + distR2New) < (distR1Old + distR2Old)) {
                            improved = true;
                            break; // break inner
                        } else {
                            // Revert
                            r2.getCustomers().remove(0);
                            r1.getCustomers().add(c);
                        }
                    }
                }
                if (improved) break;
            }
        }
    }
    
    private double calculateRouteDistance(VehicleRoute route, RoadNetwork network) {
        if (route.getCustomers().isEmpty()) return 0.0;
        double dist = 0.0;
        Location prev = route.getVehicle().getHomeDepot();
        for (Customer c : route.getCustomers()) {
            dist += network.getDistance(prev, c);
            prev = c;
        }
        dist += network.getDistance(prev, route.getVehicle().getHomeDepot());
        return dist;
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
