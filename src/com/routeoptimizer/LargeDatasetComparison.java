package com.routeoptimizer;

import java.util.Arrays;
import java.util.List;

public class LargeDatasetComparison {

    public static void main(String[] args) {

        // ========================================
        // SETTINGS
        // ========================================

        int populationSize = 50;
        int generations = 100;

        double learningRate = 0.05;
        double explorationRate = 0.20;


        // ========================================
        // LOCATIONS
        // ========================================

        Location W =
                new Location("W", "Warehouse");

        Location A =
                new Location("A", "Customer A");

        Location B =
                new Location("B", "Customer B");

        Location C =
                new Location("C", "Customer C");

        Location D =
                new Location("D", "Customer D");

        Location E =
                new Location("E", "Customer E");

        Location F =
                new Location("F", "Customer F");

        Location G =
                new Location("G", "Customer G");

        Location H =
                new Location("H", "Customer H");

        Location I =
                new Location("I", "Customer I");

        Location J =
                new Location("J", "Customer J");


        // ========================================
        // CUSTOMER LIST
        // ========================================

        List<Location> customers =
                Arrays.asList(
                        A, B, C, D, E,
                        F, G, H, I, J
                );


        // ========================================
        // ROAD NETWORK
        // ========================================

        RoadNetwork network =
                new RoadNetwork();


        // ========================================
        // WAREHOUSE -> CUSTOMERS
        // ========================================

        addRoad(network, W, A, 5, 10, 0.5, 1);
        addRoad(network, W, B, 7, 14, 0.7, 2);
        addRoad(network, W, C, 8, 16, 0.8, 2);
        addRoad(network, W, D, 6, 12, 0.6, 1);
        addRoad(network, W, E, 9, 18, 0.9, 3);
        addRoad(network, W, F, 10, 20, 1.0, 3);
        addRoad(network, W, G, 11, 22, 1.1, 4);
        addRoad(network, W, H, 12, 24, 1.2, 4);
        addRoad(network, W, I, 13, 26, 1.3, 5);
        addRoad(network, W, J, 14, 28, 1.4, 5);


        // ========================================
        // CUSTOMERS -> WAREHOUSE
        // ========================================

        addRoad(network, A, W, 5, 10, 0.5, 1);
        addRoad(network, B, W, 6, 12, 0.6, 1);
        addRoad(network, C, W, 7, 14, 0.7, 2);
        addRoad(network, D, W, 6, 12, 0.6, 1);
        addRoad(network, E, W, 8, 16, 0.8, 2);
        addRoad(network, F, W, 9, 18, 0.9, 3);
        addRoad(network, G, W, 10, 20, 1.0, 3);
        addRoad(network, H, W, 11, 22, 1.1, 4);
        addRoad(network, I, W, 12, 24, 1.2, 4);
        addRoad(network, J, W, 13, 26, 1.3, 5);


        // ========================================
        // CUSTOMER <-> CUSTOMER ROADS
        // ========================================

        addTwoWayRoad(network, A, B, 3, 6, 0.3, 1);
        addTwoWayRoad(network, A, C, 5, 10, 0.5, 2);
        addTwoWayRoad(network, A, D, 4, 8, 0.4, 1);
        addTwoWayRoad(network, A, E, 7, 14, 0.7, 2);
        addTwoWayRoad(network, A, F, 8, 16, 0.8, 3);
        addTwoWayRoad(network, A, G, 9, 18, 0.9, 3);
        addTwoWayRoad(network, A, H, 10, 20, 1.0, 4);
        addTwoWayRoad(network, A, I, 11, 22, 1.1, 4);
        addTwoWayRoad(network, A, J, 12, 24, 1.2, 5);


        addTwoWayRoad(network, B, C, 3, 6, 0.3, 1);
        addTwoWayRoad(network, B, D, 4, 8, 0.4, 1);
        addTwoWayRoad(network, B, E, 5, 10, 0.5, 2);
        addTwoWayRoad(network, B, F, 7, 14, 0.7, 2);
        addTwoWayRoad(network, B, G, 8, 16, 0.8, 3);
        addTwoWayRoad(network, B, H, 9, 18, 0.9, 3);
        addTwoWayRoad(network, B, I, 10, 20, 1.0, 4);
        addTwoWayRoad(network, B, J, 11, 22, 1.1, 4);


        addTwoWayRoad(network, C, D, 3, 6, 0.3, 1);
        addTwoWayRoad(network, C, E, 4, 8, 0.4, 1);
        addTwoWayRoad(network, C, F, 5, 10, 0.5, 2);
        addTwoWayRoad(network, C, G, 7, 14, 0.7, 2);
        addTwoWayRoad(network, C, H, 8, 16, 0.8, 3);
        addTwoWayRoad(network, C, I, 9, 18, 0.9, 3);
        addTwoWayRoad(network, C, J, 10, 20, 1.0, 4);


        addTwoWayRoad(network, D, E, 3, 6, 0.3, 1);
        addTwoWayRoad(network, D, F, 4, 8, 0.4, 1);
        addTwoWayRoad(network, D, G, 5, 10, 0.5, 2);
        addTwoWayRoad(network, D, H, 7, 14, 0.7, 2);
        addTwoWayRoad(network, D, I, 8, 16, 0.8, 3);
        addTwoWayRoad(network, D, J, 9, 18, 0.9, 3);


        addTwoWayRoad(network, E, F, 3, 6, 0.3, 1);
        addTwoWayRoad(network, E, G, 4, 8, 0.4, 1);
        addTwoWayRoad(network, E, H, 5, 10, 0.5, 2);
        addTwoWayRoad(network, E, I, 7, 14, 0.7, 2);
        addTwoWayRoad(network, E, J, 8, 16, 0.8, 3);


        addTwoWayRoad(network, F, G, 3, 6, 0.3, 1);
        addTwoWayRoad(network, F, H, 4, 8, 0.4, 1);
        addTwoWayRoad(network, F, I, 5, 10, 0.5, 2);
        addTwoWayRoad(network, F, J, 7, 14, 0.7, 2);


        addTwoWayRoad(network, G, H, 3, 6, 0.3, 1);
        addTwoWayRoad(network, G, I, 4, 8, 0.4, 1);
        addTwoWayRoad(network, G, J, 5, 10, 0.5, 2);


        addTwoWayRoad(network, H, I, 3, 6, 0.3, 1);
        addTwoWayRoad(network, H, J, 4, 8, 0.4, 1);


        addTwoWayRoad(network, I, J, 3, 6, 0.3, 1);


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
        // QUANTUM EVALUATOR
        // ========================================

        QuantumPopulationEvaluator evaluator =
                new QuantumPopulationEvaluator(
                        W,
                        network,
                        fitnessFunction
                );


        // ========================================
        // CREATE QIGA
        // ========================================

        QuantumPositionRouteGenerator
                .setExplorationRate(
                        explorationRate
                );


        PositionProbabilityUpdater updater =
                new PositionProbabilityUpdater(
                        learningRate
                );


        QIGAOptimizer qiga =
                new QIGAOptimizer(
                        populationSize,
                        customers,
                        evaluator,
                        updater
                );


        // ========================================
        // HEADER
        // ========================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "       LARGE DATASET COMPARISON"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Customers: "
                        + customers.size()
        );

        System.out.println(
                "Population Size: "
                        + populationSize
        );

        System.out.println(
                "Generations: "
                        + generations
        );

        System.out.println(
                "Learning Rate: "
                        + learningRate
        );

        System.out.println(
                "Exploration Rate: "
                        + explorationRate
        );

        System.out.println();


        // ========================================
        // RUN QIGA
        // ========================================

        System.out.println(
                "Running QIGA..."
        );

        System.out.println();


        long qigaStart =
                System.nanoTime();


        qiga.optimize(
                generations
        );


        long qigaEnd =
                System.nanoTime();


        double qigaRuntime =
                (qigaEnd - qigaStart)
                        / 1_000_000.0;


        double qigaCost =
                qiga.getBestCost();


        List<Location> qigaRoute =
                qiga.getBestRoute();


        // ========================================
        // RUN EXACT BRUTE FORCE
        // ========================================

        System.out.println();

        System.out.println(
                "Running exact brute force..."
        );

        System.out.println(
                "10 customers = 3,628,800 permutations"
        );

        System.out.println();


        BruteForceRouteOptimizer bruteForce =
                new BruteForceRouteOptimizer(
                        W,
                        network,
                        fitnessFunction
                );


        long bruteStart =
                System.nanoTime();


        bruteForce.optimize(
                customers
        );


        long bruteEnd =
                System.nanoTime();


        double bruteRuntime =
                (bruteEnd - bruteStart)
                        / 1_000_000.0;


        double exactCost =
                bruteForce.getBestCost();


        List<Location> exactRoute =
                bruteForce.getBestRoute();


        // ========================================
        // CALCULATE DIFFERENCE
        // ========================================

        double costDifference =
                qigaCost - exactCost;


        double optimalityGap =
                0.0;


        if (exactCost != 0.0) {

            optimalityGap =
                    (
                        (qigaCost - exactCost)
                        / exactCost
                    ) * 100.0;
        }


        // ========================================
        // SPEEDUP
        // ========================================

        double speedup =
                0.0;


        if (qigaRuntime > 0.0) {

            speedup =
                    bruteRuntime
                            / qigaRuntime;
        }


        // ========================================
        // 10% TOLERANCE
        // ========================================

        double allowedDifference =
                exactCost * 0.10;


        boolean withinTolerance =
                costDifference
                        <= allowedDifference;


        // ========================================
        // FINAL OUTPUT
        // ========================================

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "          FINAL COMPARISON"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "QIGA Best Route:"
        );

        printRoute(qigaRoute);


        System.out.println();

        System.out.println(
                "QIGA Best Cost: "
                        + qigaCost
        );


        System.out.println(
                "QIGA Runtime: "
                        + qigaRuntime
                        + " ms"
        );


        System.out.println();

        System.out.println(
                "Exact Best Route:"
        );

        printRoute(exactRoute);


        System.out.println();

        System.out.println(
                "Exact Best Cost: "
                        + exactCost
        );


        System.out.println(
                "Exact Runtime: "
                        + bruteRuntime
                        + " ms"
        );


        System.out.println();

        System.out.println(
                "Cost Difference: "
                        + costDifference
        );


        System.out.println(
                "Optimality Gap: "
                        + optimalityGap
                        + " %"
        );


        System.out.println(
                "Allowed 10% Difference: "
                        + allowedDifference
        );


        System.out.println(
                "Speedup: "
                        + speedup
                        + "x"
        );


        System.out.println();

        System.out.println(
                "Within 10% Tolerance: "
                        + withinTolerance
        );


        // ========================================
        // FINAL STATUS
        // ========================================

        System.out.println();

        System.out.println(
                "========================================"
        );


        if (withinTolerance) {

            System.out.println(
                    "44F VALIDATION: PASSED"
            );

            System.out.println();

            System.out.println(
                    "QIGA produced a solution within"
            );

            System.out.println(
                    "10% of the exact optimum."
            );

        } else {

            System.out.println(
                    "44F VALIDATION: FAILED"
            );

            System.out.println();

            System.out.println(
                    "QIGA exceeded the allowed"
            );

            System.out.println(
                    "10% optimality gap."
            );
        }


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

            for (Location location : route) {

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