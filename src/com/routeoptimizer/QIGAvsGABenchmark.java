package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QIGAvsGABenchmark {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("    QIGA vs CLASSICAL GA BENCHMARK");
        System.out.println("========================================");
        System.out.println();

        int numRuns = 10;
        int populationSize = 50;
        int generations = 100;
        int numCustomers = 10;
        int numVehicles = 3;

        // Build Synthetic Benchmark Instance
        BenchmarkDataset benchmark = BenchmarkDataset.createSyntheticBenchmark(
                "Benchmark-10C-3V", 1, numVehicles, 100.0, numCustomers
        );

        List<Customer> customers = benchmark.getCustomers();
        List<Vehicle> vehicles = benchmark.buildVehicles();
        Location depot = benchmark.getDepots().get(0);
        RoadNetwork network = benchmark.getRoadNetwork();
        TrafficModel trafficModel = new TrafficModel(TrafficCondition.MEDIUM);
        FleetFitnessFunction fitnessFunction = new FleetFitnessFunction();

        System.out.println("Benchmark Problem: " + benchmark.getName());
        System.out.println("Customers: " + customers.size());
        System.out.println("Vehicles: " + vehicles.size());
        System.out.println("Population: " + populationSize);
        System.out.println("Generations: " + generations);
        System.out.println("Repeated Runs: " + numRuns);
        System.out.println();

        double[] qigaFitness = new double[numRuns];
        long[] qigaRuntime = new long[numRuns];
        int[] qigaFirstBest = new int[numRuns];
        int[] qigaLastImpr = new int[numRuns];

        double[] gaFitness = new double[numRuns];
        long[] gaRuntime = new long[numRuns];
        int[] gaFirstBest = new int[numRuns];
        int[] gaLastImpr = new int[numRuns];

        for (int run = 0; run < numRuns; run++) {
            long seed = 1000L + run * 37L;

            // 1. Run QIGA
            MultiVehicleQIGAOptimizer qiga = new MultiVehicleQIGAOptimizer(
                    populationSize, customers, vehicles, depot, network, trafficModel, fitnessFunction, 0.05, 0.20, seed
            );
            FleetRoutePlan qigaPlan = qiga.optimize(generations);
            qigaFitness[run] = qigaPlan.getOverallFitness();
            qigaRuntime[run] = qiga.getOptimizationRuntimeMs();
            qigaFirstBest[run] = qiga.getFirstBestGeneration();
            qigaLastImpr[run] = qiga.getLastImprovementGeneration();

            // 2. Run Classical GA
            ClassicalGAOptimizer ga = new ClassicalGAOptimizer(
                    populationSize, customers, vehicles, depot, network, trafficModel, fitnessFunction, seed
            );
            FleetRoutePlan gaPlan = ga.optimize(generations);
            gaFitness[run] = gaPlan.getOverallFitness();
            gaRuntime[run] = ga.getOptimizationRuntimeMs();
            gaFirstBest[run] = ga.getFirstBestGeneration();
            gaLastImpr[run] = ga.getLastImprovementGeneration();

            System.out.printf("Run %2d | QIGA Fit: %7.4f (Time: %4d ms) | GA Fit: %7.4f (Time: %4d ms)%n",
                    run + 1, qigaFitness[run], qigaRuntime[run], gaFitness[run], gaRuntime[run]);
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("       10-RUN COMPARISON SUMMARY");
        System.out.println("========================================");
        System.out.println();

        printStats("QIGA", qigaFitness, qigaRuntime, qigaFirstBest, qigaLastImpr, generations);
        System.out.println("----------------------------------------");
        printStats("CLASSICAL GA", gaFitness, gaRuntime, gaFirstBest, gaLastImpr, generations);

        System.out.println();
        System.out.println("========================================");
        System.out.println("BENCHMARK COMPLETED SUCCESSFULLY");
        System.out.println("========================================");
    }

    private static void printStats(String name, double[] fitness, long[] runtime, int[] firstBest, int[] lastImpr, int totalGens) {
        double minFit = Double.MAX_VALUE, maxFit = -Double.MAX_VALUE, sumFit = 0;
        long minTime = Long.MAX_VALUE, maxTime = -1, sumTime = 0;
        int minFB = Integer.MAX_VALUE, maxFB = -1, sumFB = 0;
        int minLI = Integer.MAX_VALUE, maxLI = -1, sumLI = 0;
        double sumStag = 0;

        for (int i = 0; i < fitness.length; i++) {
            sumFit += fitness[i];
            if (fitness[i] < minFit) minFit = fitness[i];
            if (fitness[i] > maxFit) maxFit = fitness[i];

            sumTime += runtime[i];
            if (runtime[i] < minTime) minTime = runtime[i];
            if (runtime[i] > maxTime) maxTime = runtime[i];

            sumFB += firstBest[i];
            if (firstBest[i] < minFB) minFB = firstBest[i];
            if (firstBest[i] > maxFB) maxFB = firstBest[i];

            sumLI += lastImpr[i];
            if (lastImpr[i] < minLI) minLI = lastImpr[i];
            if (lastImpr[i] > maxLI) maxLI = lastImpr[i];

            sumStag += (totalGens - lastImpr[i]);
        }

        int n = fitness.length;
        System.out.println(name + " RESULTS (" + n + " runs):");
        System.out.printf("  Average Fitness: %.4f (Best: %.4f, Worst: %.4f)%n", sumFit / n, minFit, maxFit);
        System.out.printf("  Average Runtime: %.1f ms (Min: %d ms, Max: %d ms)%n", (double) sumTime / n, minTime, maxTime);
        System.out.printf("  Average First Best Gen: %.1f (Min: %d, Max: %d)%n", (double) sumFB / n, minFB, maxFB);
        System.out.printf("  Average Last Impr Gen:  %.1f (Min: %d, Max: %d)%n", (double) sumLI / n, minLI, maxLI);
        System.out.printf("  Average Final Stagnation: %.1f generations%n", sumStag / n);
    }
}
