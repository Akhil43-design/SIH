package com.routeoptimizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class Step44EScalability {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("   STEP 44I - SCALABILITY EXPERIMENT");
        System.out.println("========================================");
        System.out.println();

        int[] datasetSizes = {5, 10, 15, 20};
        int populationSize = 50;
        int generations = 100;
        double learningRate = 0.05;
        double explorationRate = 0.20;

        System.out.println("Population Size: " + populationSize);
        System.out.println("Generations: " + generations);
        System.out.println("Learning Rate: " + learningRate);
        System.out.println("Exploration Rate: " + explorationRate);
        System.out.println();

        System.out.printf("%-12s %-15s %-15s %-15s %-18s %-15s %-12s%n",
                "Customers", "Search Space", "QIGA Cost", "Exact Cost", "QIGA Runtime(ms)", "Brute Runtime", "Speedup");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (int n : datasetSizes) {
            runScalabilityTest(n, populationSize, generations, learningRate, explorationRate);
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("       SCALABILITY SUMMARY");
        System.out.println("========================================");
        System.out.println("Scalability test completed successfully.");
        System.out.println("For N >= 15, brute-force exact search is computationally impractical (O(N!)),");
        System.out.println("whereas QIGA scales polynomially with high-quality convergence.");
        System.out.println("========================================");
    }

    private static void runScalabilityTest(
            int n,
            int populationSize,
            int generations,
            double learningRate,
            double explorationRate) {

        Location warehouse = new Location("W", "Central Warehouse");
        List<Location> customers = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            customers.add(new Location("C" + i, "Customer " + i));
        }

        RoadNetwork network = new RoadNetwork();

        // Warehouse <-> Customers
        for (int i = 0; i < n; i++) {
            Location cust = customers.get(i);
            double dist = 4.0 + (i % 7);
            double time = dist * 2.0;
            double fuel = dist * 0.1;
            int traffic = 1 + (i % 3);

            network.addRoad(new Road(warehouse, cust, dist, time, fuel, traffic));
            network.addRoad(new Road(cust, warehouse, dist, time, fuel, traffic));
        }

        // Customer <-> Customer complete network
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                Location c1 = customers.get(i);
                Location c2 = customers.get(j);
                double dist = 2.0 + Math.abs(i - j) + ((i * 3 + j) % 4);
                double time = dist * 1.8;
                double fuel = dist * 0.09;
                int traffic = 1 + ((i + j) % 4);

                network.addRoad(new Road(c1, c2, dist, time, fuel, traffic));
            }
        }

        FitnessFunction fitnessFunction = new FitnessFunction(
                0.25, 0.30, 0.20, 0.25,
                30.0 * n, 60.0 * n, 4.0 * n, 5.0 * n
        );

        QuantumPopulationEvaluator evaluator = new QuantumPopulationEvaluator(
                warehouse, network, fitnessFunction
        );

        PositionProbabilityUpdater updater = new PositionProbabilityUpdater(learningRate);
        QuantumPositionRouteGenerator.setExplorationRate(explorationRate);

        QIGAOptimizer optimizer = new QIGAOptimizer(
                populationSize,
                customers,
                evaluator,
                updater
        );

        // Run QIGA
        long qigaStart = System.nanoTime();
        optimizer.optimize(generations);
        long qigaEnd = System.nanoTime();

        double qigaRuntime = (qigaEnd - qigaStart) / 1_000_000.0;
        double qigaCost = optimizer.getBestCost();
        List<Location> qigaRoute = optimizer.getBestRoute();

        // Validate Permutation
        boolean valid = qigaRoute != null &&
                qigaRoute.size() == n &&
                new HashSet<>(qigaRoute).size() == n &&
                qigaRoute.containsAll(customers);

        if (!valid) {
            throw new IllegalStateException("QIGA produced invalid route for N = " + n);
        }

        // Run Brute Force if feasible (N <= 10)
        String searchSpace = getSearchSpaceString(n);
        String exactCostStr = "N/A";
        String bruteRuntimeStr = "Impractical";
        String speedupStr = "N/A";

        if (n <= 10) {
            BruteForceRouteOptimizer bruteForce = new BruteForceRouteOptimizer(
                    warehouse, network, fitnessFunction
            );

            long bruteStart = System.nanoTime();
            bruteForce.optimize(customers);
            long bruteEnd = System.nanoTime();

            double bruteRuntime = (bruteEnd - bruteStart) / 1_000_000.0;
            double exactCost = bruteForce.getBestCost();

            exactCostStr = String.format(Locale.US, "%.4f", exactCost);
            bruteRuntimeStr = String.format(Locale.US, "%.1f ms", bruteRuntime);
            double speedup = bruteRuntime / qigaRuntime;
            speedupStr = String.format(Locale.US, "%.2fx", speedup);
        }

        System.out.printf(Locale.US, "%-12d %-15s %-15.4f %-15s %-18.1f %-15s %-12s%n",
                n, searchSpace, qigaCost, exactCostStr, qigaRuntime, bruteRuntimeStr, speedupStr);
    }

    private static String getSearchSpaceString(int n) {
        if (n == 5) return "120 (5!)";
        if (n == 10) return "3.63M (10!)";
        if (n == 15) return "1.31T (15!)";
        if (n == 20) return "2.43E18 (20!)";
        return n + "!";
    }
}
