package com.routeoptimizer;

import java.util.Arrays;
import java.util.List;

public class RealisticDatasetTest {

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

        Location warehouse =
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

        addRoad(network, warehouse, A, 5, 10, 0.5, 1);
        addRoad(network, warehouse, B, 7, 14, 0.7, 2);
        addRoad(network, warehouse, C, 8, 16, 0.8, 2);
        addRoad(network, warehouse, D, 6, 12, 0.6, 1);
        addRoad(network, warehouse, E, 9, 18, 0.9, 3);
        addRoad(network, warehouse, F, 10, 20, 1.0, 3);
        addRoad(network, warehouse, G, 11, 22, 1.1, 4);
        addRoad(network, warehouse, H, 12, 24, 1.2, 4);
        addRoad(network, warehouse, I, 13, 26, 1.3, 5);
        addRoad(network, warehouse, J, 14, 28, 1.4, 5);


        // ========================================
        // CUSTOMERS -> WAREHOUSE
        // ========================================

        addRoad(network, A, warehouse, 5, 10, 0.5, 1);
        addRoad(network, B, warehouse, 6, 12, 0.6, 1);
        addRoad(network, C, warehouse, 7, 14, 0.7, 2);
        addRoad(network, D, warehouse, 6, 12, 0.6, 1);
        addRoad(network, E, warehouse, 8, 16, 0.8, 2);
        addRoad(network, F, warehouse, 9, 18, 0.9, 3);
        addRoad(network, G, warehouse, 10, 20, 1.0, 3);
        addRoad(network, H, warehouse, 11, 22, 1.1, 4);
        addRoad(network, I, warehouse, 12, 24, 1.2, 4);
        addRoad(network, J, warehouse, 13, 26, 1.3, 5);


        // ========================================
        // CUSTOMER CONNECTIONS
        // BOTH DIRECTIONS ARE ADDED
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
        // EVALUATOR
        // ========================================

        QuantumPopulationEvaluator evaluator =
                new QuantumPopulationEvaluator(
                        warehouse,
                        network,
                        fitnessFunction
                );


        // ========================================
        // QIGA SETTINGS
        // ========================================

        QuantumPositionRouteGenerator
                .setExplorationRate(
                        explorationRate
                );


        PositionProbabilityUpdater updater =
                new PositionProbabilityUpdater(
                        learningRate
                );


        // ========================================
        // CREATE OPTIMIZER
        // ========================================

        QIGAOptimizer optimizer =
                new QIGAOptimizer(
                        populationSize,
                        customers,
                        evaluator,
                        updater
                );


        // ========================================
        // DISPLAY
        // ========================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "      REALISTIC DATASET TEST"
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

        long startTime =
                System.nanoTime();

        optimizer.optimize(
                generations
        );

        long endTime =
                System.nanoTime();


        double runtimeMs =
                (endTime - startTime)
                        / 1_000_000.0;


        // ========================================
        // RESULT
        // ========================================

        List<Location> bestRoute =
                optimizer.getBestRoute();

        double bestCost =
                optimizer.getBestCost();


        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "        FINAL DATASET RESULT"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Best Customer Order:"
        );

        System.out.println(
                bestRoute
        );

        System.out.println();

        System.out.println(
                "Complete Route:"
        );

        System.out.print("W");

        for (Location location :
                bestRoute) {

            System.out.print(
                    " -> "
                            + location.getId()
            );
        }

        System.out.println(" -> W");

        System.out.println();

        System.out.println(
                "Best Cost: "
                        + bestCost
        );

        System.out.println();

        System.out.println(
                "Runtime: "
                        + runtimeMs
                        + " ms"
        );

        System.out.println();

        System.out.println(
                "Generations Without Improvement: "
                        + optimizer
                                .getGenerationsWithoutImprovement()
        );

        System.out.println();

        System.out.println(
                "========================================"
        );
    }


    // ========================================
    // ADD ONE ROAD
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

        /*
         * First direction
         */

        addRoad(
                network,
                first,
                second,
                distance,
                time,
                fuel,
                traffic
        );


        /*
         * Reverse direction
         */

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
}