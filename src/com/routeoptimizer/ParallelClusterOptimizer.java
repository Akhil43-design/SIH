package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelClusterOptimizer {

    private final ExecutorService executorService;
    private final boolean deterministicMode;

    public ParallelClusterOptimizer(int maxConcurrentThreads, boolean deterministicMode) {
        this.deterministicMode = deterministicMode;
        if (deterministicMode) {
            // For deterministic, we still can run sequentially if parallelism breaks determinism
            // However, independent clusters can be optimized in parallel IF they don't share state.
            // To guarantee 100% determinism, sequential is safest, but we'll use a pool of 1 if strict.
            this.executorService = Executors.newFixedThreadPool(1);
        } else {
            this.executorService = Executors.newFixedThreadPool(maxConcurrentThreads);
        }
    }

    public List<FleetRoutePlan> optimizeClusters(List<Callable<FleetRoutePlan>> optimizationTasks) {
        List<FleetRoutePlan> results = new ArrayList<>();
        
        if (deterministicMode) {
            // Run sequentially for strict determinism
            for (Callable<FleetRoutePlan> task : optimizationTasks) {
                try {
                    results.add(task.call());
                } catch (Exception e) {
                    throw new RuntimeException("Optimization task failed", e);
                }
            }
        } else {
            // Run in parallel
            try {
                List<Future<FleetRoutePlan>> futures = executorService.invokeAll(optimizationTasks);
                for (Future<FleetRoutePlan> f : futures) {
                    results.add(f.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Parallel cluster optimization failed", e);
            }
        }
        
        return results;
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
