package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RepeatabilityTest {

    // ========================================
    // EXPERIMENT SETTINGS
    // ========================================

    private static final int RUNS = 10;

    private static final int POPULATION_SIZE = 50;

    private static final int GENERATIONS = 100;

    private static final double LEARNING_RATE = 0.05;

    private static final double EXPLORATION_RATE = 0.20;


    // ========================================
    // MAIN
    // ========================================

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println();
        System.out.println("========================================");
        System.out.println("       44G REPEATABILITY TEST");
        System.out.println("========================================");
        System.out.println();

        System.out.println(
                "Number of Runs: "
                        + RUNS
        );

        System.out.println(
                "Customers: 10"
        );

        System.out.println(
                "Population Size: "
                        + POPULATION_SIZE
        );

        System.out.println(
                "Generations: "
                        + GENERATIONS
        );

        System.out.println(
                "Learning Rate: "
                        + LEARNING_RATE
        );

        System.out.println(
                "Exploration Rate: "
                        + EXPLORATION_RATE
        );

        System.out.println();


        // ========================================
        // CREATE DATASET
        // ========================================

        Location W =
                new Location(
                        "W",
                        "Warehouse"
                );

        Location A =
                new Location(
                        "A",
                        "Customer A"
                );

        Location B =
                new Location(
                        "B",
                        "Customer B"
                );

        Location C =
                new Location(
                        "C",
                        "Customer C"
                );

        Location D =
                new Location(
                        "D",
                        "Customer D"
                );

        Location E =
                new Location(
                        "E",
                        "Customer E"
                );

        Location F =
                new Location(
                        "F",
                        "Customer F"
                );

        Location G =
                new Location(
                        "G",
                        "Customer G"
                );

        Location H =
                new Location(
                        "H",
                        "Customer H"
                );

        Location I =
                new Location(
                        "I",
                        "Customer I"
                );

        Location J =
                new Location(
                        "J",
                        "Customer J"
                );


        // ========================================
        // CUSTOMER LIST
        // ========================================

        List<Location> customers =
                Arrays.asList(
                        A,
                        B,
                        C,
                        D,
                        E,
                        F,
                        G,
                        H,
                        I,
                        J
                );


        // ========================================
        // ROAD NETWORK
        // ========================================

        RoadNetwork network =
                new RoadNetwork();


        // ========================================
        // WAREHOUSE -> CUSTOMERS
        // ========================================

        addRoad(
                network,
                W,
                A,
                5,
                10,
                0.5,
                1
        );

        addRoad(
                network,
                W,
                B,
                7,
                14,
                0.7,
                2
        );

        addRoad(
                network,
                W,
                C,
                8,
                16,
                0.8,
                2
        );

        addRoad(
                network,
                W,
                D,
                6,
                12,
                0.6,
                1
        );

        addRoad(
                network,
                W,
                E,
                9,
                18,
                0.9,
                3
        );

        addRoad(
                network,
                W,
                F,
                10,
                20,
                1.0,
                3
        );

        addRoad(
                network,
                W,
                G,
                11,
                22,
                1.1,
                4
        );

        addRoad(
                network,
                W,
                H,
                12,
                24,
                1.2,
                4
        );

        addRoad(
                network,
                W,
                I,
                13,
                26,
                1.3,
                5
        );

        addRoad(
                network,
                W,
                J,
                14,
                28,
                1.4,
                5
        );


        // ========================================
        // CUSTOMERS -> WAREHOUSE
        // ========================================

        addRoad(
                network,
                A,
                W,
                5,
                10,
                0.5,
                1
        );

        addRoad(
                network,
                B,
                W,
                6,
                12,
                0.6,
                1
        );

        addRoad(
                network,
                C,
                W,
                7,
                14,
                0.7,
                2
        );

        addRoad(
                network,
                D,
                W,
                6,
                12,
                0.6,
                1
        );

        addRoad(
                network,
                E,
                W,
                8,
                16,
                0.8,
                2
        );

        addRoad(
                network,
                F,
                W,
                9,
                18,
                0.9,
                3
        );

        addRoad(
                network,
                G,
                W,
                10,
                20,
                1.0,
                3
        );

        addRoad(
                network,
                H,
                W,
                11,
                22,
                1.1,
                4
        );

        addRoad(
                network,
                I,
                W,
                12,
                24,
                1.2,
                4
        );

        addRoad(
                network,
                J,
                W,
                13,
                26,
                1.3,
                5
        );


        // ========================================
        // CUSTOMER <-> CUSTOMER
        // ========================================

        addTwoWayRoad(
                network, A, B,
                3, 6, 0.3, 1
        );

        addTwoWayRoad(
                network, A, C,
                5, 10, 0.5, 2
        );

        addTwoWayRoad(
                network, A, D,
                4, 8, 0.4, 1
        );

        addTwoWayRoad(
                network, A, E,
                7, 14, 0.7, 2
        );

        addTwoWayRoad(
                network, A, F,
                8, 16, 0.8, 3
        );

        addTwoWayRoad(
                network, A, G,
                9, 18, 0.9, 3
        );

        addTwoWayRoad(
                network, A, H,
                10, 20, 1.0, 4
        );

        addTwoWayRoad(
                network, A, I,
                11, 22, 1.1, 4
        );

        addTwoWayRoad(
                network, A, J,
                12, 24, 1.2, 5
        );


        addTwoWayRoad(
                network, B, C,
                3, 6, 0.3, 1
        );

        addTwoWayRoad(
                network, B, D,
                4, 8, 0.4, 1
        );

        addTwoWayRoad(
                network, B, E,
                5, 10, 0.5, 2
        );

        addTwoWayRoad(
                network, B, F,
                7, 14, 0.7, 2
        );

        addTwoWayRoad(
                network, B, G,
                8, 16, 0.8, 3
        );

        addTwoWayRoad(
                network, B, H,
                9, 18, 0.9, 3
        );

        addTwoWayRoad(
                network, B, I,
                10, 20, 1.0, 4
        );

        addTwoWayRoad(
                network, B, J,
                11, 22, 1.1, 4
        );


        addTwoWayRoad(
                network, C, D,
                3, 6, 0.3, 1
        );

        addTwoWayRoad(
                network, C, E,
                4, 8, 0.4, 1
        );

        addTwoWayRoad(
                network, C, F,
                5, 10, 0.5, 2
        );

        addTwoWayRoad(
                network, C, G,
                7, 14, 0.7, 2
        );

        addTwoWayRoad(
                network, C, H,
                8, 16, 0.8, 3
        );

        addTwoWayRoad(
                network, C, I,
                9, 18, 0.9, 3
        );

        addTwoWayRoad(
                network, C, J,
                10, 20, 1.0, 4
        );


        addTwoWayRoad(
                network, D, E,
                3, 6, 0.3, 1
        );

        addTwoWayRoad(
                network, D, F,
                4, 8, 0.4, 1
        );

        addTwoWayRoad(
                network, D, G,
                5, 10, 0.5, 2
        );

        addTwoWayRoad(
                network, D, H,
                7, 14, 0.7, 2
        );

        addTwoWayRoad(
                network, D, I,
                8, 16, 0.8, 3
        );

        addTwoWayRoad(
                network, D, J,
                9, 18, 0.9, 3
        );


        addTwoWayRoad(
                network, E, F,
                3, 6, 0.3, 1
        );

        addTwoWayRoad(
                network, E, G,
                4, 8, 0.4, 1
        );

        addTwoWayRoad(
                network, E, H,
                5, 10, 0.5, 2
        );

        addTwoWayRoad(
                network, E, I,
                7, 14, 0.7, 2
        );

        addTwoWayRoad(
                network, E, J,
                8, 16, 0.8, 3
        );


        addTwoWayRoad(
                network, F, G,
                3, 6, 0.3, 1
        );

        addTwoWayRoad(
                network, F, H,
                4, 8, 0.4, 1
        );

        addTwoWayRoad(
                network, F, I,
                5, 10, 0.5, 2
        );

        addTwoWayRoad(
                network, F, J,
                7, 14, 0.7, 2
        );


        addTwoWayRoad(
                network, G, H,
                3, 6, 0.3, 1
        );

        addTwoWayRoad(
                network, G, I,
                4, 8, 0.4, 1
        );

        addTwoWayRoad(
                network, G, J,
                5, 10, 0.5, 2
        );


        addTwoWayRoad(
                network, H, I,
                3, 6, 0.3, 1
        );

        addTwoWayRoad(
                network, H, J,
                4, 8, 0.4, 1
        );


        addTwoWayRoad(
                network, I, J,
                3, 6, 0.3, 1
        );


        // ========================================
        // FITNESS FUNCTION
        // ========================================

        FitnessFunction fitnessFunction =
                new FitnessFunction(
                        0.25,
                        0.30,
                        0.20,
                        0.25,
                        150.0,
                        300.0,
                        20.0,
                        50.0
                );


        // ========================================
        // EVALUATOR
        // ========================================

        QuantumPopulationEvaluator evaluator =
                new QuantumPopulationEvaluator(
                        W,
                        network,
                        fitnessFunction
                );


        // ========================================
        // EXACT BRUTE FORCE
        // ========================================

        System.out.println(
                "Calculating exact optimum..."
        );

        long bruteStart =
                System.nanoTime();


        BruteForceRouteOptimizer bruteForce =
                new BruteForceRouteOptimizer(
                        W,
                        network,
                        fitnessFunction
                );


        bruteForce.optimize(
                customers
        );


        long bruteEnd =
                System.nanoTime();


        double exactRuntime =
                (bruteEnd - bruteStart)
                        / 1_000_000.0;


        double exactCost =
                bruteForce.getBestCost();


        List<Location> exactRoute =
                bruteForce.getBestRoute();


        System.out.println();

        System.out.println(
                "Exact Best Cost: "
                        + exactCost
        );

        System.out.println(
                "Exact Runtime: "
                        + exactRuntime
                        + " ms"
        );

        System.out.println();

        System.out.println(
                "Exact Route:"
        );

        printRoute(exactRoute);

        System.out.println();


        // ========================================
        // STATISTICS
        // ========================================

        double totalCost = 0.0;

        double bestCost =
                Double.MAX_VALUE;

        double worstCost =
                -Double.MAX_VALUE;


        double totalGap = 0.0;

        double bestGap =
                Double.MAX_VALUE;

        double worstGap =
                -Double.MAX_VALUE;


        double totalRuntime = 0.0;

        double bestRuntime =
                Double.MAX_VALUE;

        double worstRuntime =
                -Double.MAX_VALUE;


        double totalSpeedup = 0.0;

        double bestSpeedup =
                Double.MAX_VALUE;

        double worstSpeedup =
                -Double.MAX_VALUE;


        int exactMatches = 0;

        int toleranceSuccesses = 0;


        int totalFirstBestGen = 0;

        int minFirstBestGen =
                Integer.MAX_VALUE;

        int maxFirstBestGen =
                Integer.MIN_VALUE;


        int totalLastImprovementGen = 0;

        int minLastImprovementGen =
                Integer.MAX_VALUE;

        int maxLastImprovementGen =
                Integer.MIN_VALUE;


        int totalFinalStagnation = 0;

        int minFinalStagnation =
                Integer.MAX_VALUE;

        int maxFinalStagnation =
                Integer.MIN_VALUE;


        int totalRoutesEvaluatedAcrossRuns = 0;

        int totalRoutesImprovedAcrossRuns = 0;

        int totalImprovementsAcrossRuns = 0;

        double totalCostImprovementAcrossRuns = 0.0;


        // ========================================
        // RUN QIGA
        // ========================================

        for (int run = 1;
             run <= RUNS;
             run++) {


            System.out.println(
                    "----------------------------------------"
            );

            System.out.println(
                    "Run " + run + " / " + RUNS
            );

            System.out.println(
                    "----------------------------------------"
            );


            /*
             * Create a fresh evaluator.
             */

            QuantumPopulationEvaluator
                    runEvaluator =
                    new QuantumPopulationEvaluator(
                            W,
                            network,
                            fitnessFunction
                    );


            /*
             * Create a fresh probability updater.
             */

            PositionProbabilityUpdater updater =
                    new PositionProbabilityUpdater(
                            LEARNING_RATE
                    );


            /*
             * Create a completely new QIGA.
             */

            QIGAOptimizer qiga =
                    new QIGAOptimizer(
                            POPULATION_SIZE,
                            customers,
                            runEvaluator,
                            updater
                    );


            /*
             * Make sure exploration starts
             * from the requested value.
             *
             * QIGAOptimizer itself starts at
             * MAX_EXPLORATION = 0.30.
             *
             * We explicitly set the requested
             * starting value here.
             */

            QuantumPositionRouteGenerator
                    .setExplorationRate(
                            EXPLORATION_RATE
                    );


            // ------------------------------------
            // RUN
            // ------------------------------------

            long start =
                    System.nanoTime();


            qiga.optimize(
                    GENERATIONS
            );


            long end =
                    System.nanoTime();


            double runtime =
                    (end - start)
                            / 1_000_000.0;


            double qigaCost =
                    qiga.getBestCost();


            List<Location> qigaRoute =
                    qiga.getBestRoute();


            // ------------------------------------
            // GAP
            // ------------------------------------

            double gap = 0.0;


            if (exactCost != 0.0) {

                gap =
                        (
                            (qigaCost - exactCost)
                            / exactCost
                        ) * 100.0;
            }


            // ------------------------------------
            // SPEEDUP
            // ------------------------------------

            double speedup =
                    0.0;


            if (runtime > 0.0) {

                speedup =
                        exactRuntime
                                / runtime;
            }


            // ------------------------------------
            // TOLERANCE
            // ------------------------------------

            boolean withinTolerance =
                    qigaCost
                            <=
                    exactCost * 1.10;


            // ------------------------------------
            // EXACT MATCH
            // ------------------------------------

            boolean exactMatch =
                    Math.abs(
                            qigaCost
                                    - exactCost
                    )
                    < 0.000000001;


            if (withinTolerance) {

                toleranceSuccesses++;
            }


            if (exactMatch) {

                exactMatches++;
            }


            // ------------------------------------
            // STATISTICS
            // ------------------------------------

            totalCost += qigaCost;


            if (qigaCost < bestCost) {

                bestCost =
                        qigaCost;
            }


            if (qigaCost > worstCost) {

                worstCost =
                        qigaCost;
            }


            totalGap += gap;


            if (gap < bestGap) {

                bestGap =
                        gap;
            }


            if (gap > worstGap) {

                worstGap =
                        gap;
            }


            totalRuntime += runtime;


            if (runtime < bestRuntime) {

                bestRuntime =
                        runtime;
            }


            if (runtime > worstRuntime) {

                worstRuntime =
                        runtime;
            }


            totalSpeedup += speedup;


            if (speedup < bestSpeedup) {

                bestSpeedup =
                        speedup;
            }


            if (speedup > worstSpeedup) {

                worstSpeedup =
                        speedup;
            }


            int firstBestGeneration =
                    qiga.getFirstBestGeneration();

            int lastImprovementGeneration =
                    qiga.getLastImprovementGeneration();

            int finalStagnation =
                    GENERATIONS
                            - lastImprovementGeneration;


            totalFirstBestGen +=
                    firstBestGeneration;

            if (firstBestGeneration < minFirstBestGen) {
                minFirstBestGen = firstBestGeneration;
            }

            if (firstBestGeneration > maxFirstBestGen) {
                maxFirstBestGen = firstBestGeneration;
            }


            totalLastImprovementGen +=
                    lastImprovementGeneration;

            if (lastImprovementGeneration < minLastImprovementGen) {
                minLastImprovementGen = lastImprovementGeneration;
            }

            if (lastImprovementGeneration > maxLastImprovementGen) {
                maxLastImprovementGen = lastImprovementGeneration;
            }


            totalFinalStagnation +=
                    finalStagnation;

            if (finalStagnation < minFinalStagnation) {
                minFinalStagnation = finalStagnation;
            }

            if (finalStagnation > maxFinalStagnation) {
                maxFinalStagnation = finalStagnation;
            }


            totalRoutesEvaluatedAcrossRuns +=
                    qiga.getTotalRoutesEvaluated();

            totalRoutesImprovedAcrossRuns +=
                    qiga.getRoutesImproved();

            totalImprovementsAcrossRuns +=
                    qiga.getTotalImprovements();

            totalCostImprovementAcrossRuns +=
                    qiga.getTotalCostImprovement();


            // ------------------------------------
            // RUN RESULT
            // ------------------------------------

            System.out.println();

            System.out.println(
                    "QIGA Best Cost: "
                            + qigaCost
            );

            System.out.println(
                    "Optimality Gap: "
                            + gap
                            + " %"
            );

            System.out.println(
                    "Runtime: "
                            + runtime
                            + " ms"
            );

            System.out.println(
                    "Speedup: "
                            + speedup
                            + "x"
            );

            System.out.println(
                    "Exact Match: "
                            + exactMatch
            );

            System.out.println(
                    "Within 10%: "
                            + withinTolerance
            );

            System.out.println();

            System.out.println(
                    "First Best Generation: "
                            + firstBestGeneration
            );

            System.out.println(
                    "Last Improvement Generation: "
                            + lastImprovementGeneration
            );

            System.out.println(
                    "Final Stagnation: "
                            + finalStagnation
            );

            System.out.println();

            System.out.println(
                    "Route: "
                            + qigaRoute
            );

            System.out.println();
        }


        // ========================================
        // AVERAGES
        // ========================================

        double averageCost =
                totalCost / RUNS;

        double averageGap =
                totalGap / RUNS;

        double averageRuntime =
                totalRuntime / RUNS;

        double averageSpeedup =
                totalSpeedup / RUNS;

        double successRate =
                ((double) toleranceSuccesses
                        / RUNS)
                        * 100.0;

        double exactMatchRate =
                ((double) exactMatches
                        / RUNS)
                        * 100.0;

        double averageFirstBestGen =
                (double) totalFirstBestGen
                        / RUNS;

        double averageLastImprovementGen =
                (double) totalLastImprovementGen
                        / RUNS;

        double averageFinalStagnation =
                (double) totalFinalStagnation
                        / RUNS;

        double overallImprovementRate =
                totalRoutesEvaluatedAcrossRuns > 0
                        ? ((double) totalRoutesImprovedAcrossRuns
                                / totalRoutesEvaluatedAcrossRuns)
                                * 100.0
                        : 0.0;

        double overallAverageImprovement =
                totalRoutesImprovedAcrossRuns > 0
                        ? totalCostImprovementAcrossRuns
                                / totalRoutesImprovedAcrossRuns
                        : 0.0;


        // ========================================
        // FINAL REPORT
        // ========================================

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "       44G FINAL STATISTICS"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "EXACT SOLUTION"
        );

        System.out.println(
                "Exact Cost: "
                        + exactCost
        );

        System.out.println(
                "Exact Runtime: "
                        + exactRuntime
                        + " ms"
        );

        System.out.println(
                "Exact Route:"
        );

        printRoute(exactRoute);

        System.out.println();


        System.out.println(
                "QIGA RESULTS"
        );

        System.out.println(
                "Best QIGA Cost: "
                        + bestCost
        );

        System.out.println(
                "Worst QIGA Cost: "
                        + worstCost
        );

        System.out.println(
                "Average QIGA Cost: "
                        + averageCost
        );

        System.out.println();


        System.out.println(
                "OPTIMALITY GAP"
        );

        System.out.println(
                "Best Gap: "
                        + bestGap
                        + " %"
        );

        System.out.println(
                "Worst Gap: "
                        + worstGap
                        + " %"
        );

        System.out.println(
                "Average Gap: "
                        + averageGap
                        + " %"
        );

        System.out.println();


        System.out.println(
                "RUNTIME"
        );

        System.out.println(
                "Best QIGA Runtime: "
                        + bestRuntime
                        + " ms"
        );

        System.out.println(
                "Worst QIGA Runtime: "
                        + worstRuntime
                        + " ms"
        );

        System.out.println(
                "Average QIGA Runtime: "
                        + averageRuntime
                        + " ms"
        );

        System.out.println();


        System.out.println(
                "SPEEDUP"
        );

        System.out.println(
                "Best Speedup: "
                        + bestSpeedup
                        + "x"
        );

        System.out.println(
                "Worst Speedup: "
                        + worstSpeedup
                        + "x"
        );

        System.out.println(
                "Average Speedup: "
                        + averageSpeedup
                        + "x"
        );

        System.out.println();


        System.out.println(
                "RELIABILITY"
        );

        System.out.println(
                "Exact Matches: "
                        + exactMatches
                        + " / "
                        + RUNS
        );

        System.out.println(
                "Exact Match Rate: "
                        + exactMatchRate
                        + " %"
        );

        System.out.println(
                "Within 10% Runs: "
                        + toleranceSuccesses
                        + " / "
                        + RUNS
        );

        System.out.println(
                "Success Rate: "
                        + successRate
                        + " %"
        );

        System.out.println();


        // ========================================
        // VALIDATION
        // ========================================

        boolean passed =
                successRate >= 90.0;


        if (passed) {

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "       44G VALIDATION: PASSED"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println();

            System.out.println(
                    "QIGA successfully produced"
            );

            System.out.println(
                    "solutions within 10% of the"
            );

            System.out.println(
                    "exact optimum in at least 90%"
            );

            System.out.println(
                    "of the repeated runs."
            );

        } else {

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "       44G VALIDATION: FAILED"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println();

            System.out.println(
                    "The repeatability requirement"
            );

            System.out.println(
                    "was not satisfied."
            );
        }


        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "       CONVERGENCE STATISTICS"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Average First Best Generation: "
                        + averageFirstBestGen
        );

        System.out.println(
                "Minimum First Best Generation: "
                        + minFirstBestGen
        );

        System.out.println(
                "Maximum First Best Generation: "
                        + maxFirstBestGen
        );

        System.out.println();

        System.out.println(
                "Average Last Improvement Generation: "
                        + averageLastImprovementGen
        );

        System.out.println(
                "Minimum Last Improvement Generation: "
                        + minLastImprovementGen
        );

        System.out.println(
                "Maximum Last Improvement Generation: "
                        + maxLastImprovementGen
        );

        System.out.println();

        System.out.println(
                "Average Final Stagnation: "
                        + averageFinalStagnation
        );

        System.out.println(
                "Minimum Final Stagnation: "
                        + minFinalStagnation
        );

        System.out.println(
                "Maximum Final Stagnation: "
                        + maxFinalStagnation
        );

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "       LOCAL SEARCH STATISTICS"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Routes Improved: "
                        + totalRoutesImprovedAcrossRuns
        );

        System.out.println(
                "Total Improvements: "
                        + totalImprovementsAcrossRuns
        );

        System.out.println(
                "Improvement Rate: "
                        + overallImprovementRate
                        + " %"
        );

        System.out.println(
                "Average Improvement: "
                        + overallAverageImprovement
        );

        System.out.println();

        System.out.println(
                "========================================"
        );
    }


    // ========================================
    // ADD ROAD
    // ========================================

    private static void addRoad(
            RoadNetwork network,
            Location from,
            Location to,
            double distance,
            double time,
            double fuel,
            int traffic) {

        network.addRoad(
                new Road(
                        from,
                        to,
                        distance,
                        time,
                        fuel,
                        traffic
                )
        );
    }


    // ========================================
    // ADD TWO-WAY ROAD
    // ========================================

    private static void addTwoWayRoad(
            RoadNetwork network,
            Location first,
            Location second,
            double distance,
            double time,
            double fuel,
            int traffic) {

        addRoad(
                network,
                first,
                second,
                distance,
                time,
                fuel,
                traffic
        );

        addRoad(
                network,
                second,
                first,
                distance,
                time,
                fuel,
                traffic
        );
    }


    // ========================================
    // PRINT ROUTE
    // ========================================

    private static void printRoute(
            List<Location> route) {

        System.out.print("W");

        if (route != null) {

            for (Location location :
                    route) {

                System.out.print(
                        " -> "
                                + location.getId()
                );
            }
        }

        System.out.println(
                " -> W"
        );
    }
}