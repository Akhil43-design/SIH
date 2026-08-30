package com.routeoptimizer;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ========================================
        // 1. CREATE LOCATIONS
        // ========================================

        Location warehouse =
                new Location(
                        "W",
                        "Warehouse"
                );

        Location customerA =
                new Location(
                        "A",
                        "Customer A"
                );

        Location customerB =
                new Location(
                        "B",
                        "Customer B"
                );

        Location customerC =
                new Location(
                        "C",
                        "Customer C"
                );

        List<Location> customers =
                Arrays.asList(
                        customerA,
                        customerB,
                        customerC
                );


        // ========================================
        // 2. CREATE ROAD NETWORK
        // ========================================

        RoadNetwork network =
                new RoadNetwork();


        // ----------------------------------------
        // Warehouse → Customers
        // ----------------------------------------

        network.addRoad(
                new Road(
                        warehouse,
                        customerA,
                        5.0,
                        10.0,
                        0.5,
                        1
                )
        );

        network.addRoad(
                new Road(
                        warehouse,
                        customerB,
                        8.0,
                        15.0,
                        0.8,
                        2
                )
        );

        network.addRoad(
                new Road(
                        warehouse,
                        customerC,
                        7.0,
                        14.0,
                        0.7,
                        2
                )
        );


        // ----------------------------------------
        // Customer → Customer
        // ----------------------------------------

        network.addRoad(
                new Road(
                        customerA,
                        customerB,
                        4.0,
                        8.0,
                        0.4,
                        1
                )
        );

        network.addRoad(
                new Road(
                        customerA,
                        customerC,
                        6.0,
                        12.0,
                        0.6,
                        2
                )
        );

        network.addRoad(
                new Road(
                        customerB,
                        customerA,
                        4.0,
                        8.0,
                        0.4,
                        1
                )
        );

        network.addRoad(
                new Road(
                        customerB,
                        customerC,
                        3.0,
                        6.0,
                        0.3,
                        1
                )
        );

        network.addRoad(
                new Road(
                        customerC,
                        customerA,
                        6.0,
                        12.0,
                        0.6,
                        2
                )
        );

        network.addRoad(
                new Road(
                        customerC,
                        customerB,
                        3.0,
                        6.0,
                        0.3,
                        1
                )
        );


        // ----------------------------------------
        // Customer → Warehouse
        // ----------------------------------------

        network.addRoad(
                new Road(
                        customerA,
                        warehouse,
                        5.0,
                        10.0,
                        0.5,
                        1
                )
        );

        network.addRoad(
                new Road(
                        customerB,
                        warehouse,
                        6.0,
                        12.0,
                        0.6,
                        1
                )
        );

        network.addRoad(
                new Road(
                        customerC,
                        warehouse,
                        7.0,
                        14.0,
                        0.7,
                        2
                )
        );


        // ========================================
        // 3. FITNESS FUNCTION
        // ========================================

        FitnessFunction fitnessFunction =
                new FitnessFunction(
                        0.25,   // Distance weight
                        0.30,   // Time weight
                        0.20,   // Fuel weight
                        0.25,   // Traffic weight

                        50.0,   // Distance normalization
                        100.0,  // Time normalization
                        10.0,   // Fuel normalization
                        12.0    // Traffic normalization
                );


        // ========================================
        // 4. QUANTUM POPULATION EVALUATOR
        // ========================================

        QuantumPopulationEvaluator evaluator =
                new QuantumPopulationEvaluator(
                        warehouse,
                        network,
                        fitnessFunction
                );


        // ========================================
        // 5. EXPLORATION CONTROL
        // ========================================

        double explorationRate = 0.20;

        QuantumPositionRouteGenerator
                .setExplorationRate(
                        explorationRate
                );


        // ========================================
        // 6. PROBABILITY UPDATER
        // ========================================

        double learningRate = 0.05;

        PositionProbabilityUpdater updater =
                new PositionProbabilityUpdater(
                        learningRate
                );


        // ========================================
        // 7. QIGA SETTINGS
        // ========================================

        int populationSize = 30;

        int generations = 50;


        // ========================================
        // 8. CREATE QIGA OPTIMIZER
        // ========================================

        QIGAOptimizer optimizer =
                new QIGAOptimizer(
                        populationSize,
                        customers,
                        evaluator,
                        updater
                );


        // ========================================
        // 9. DISPLAY SETTINGS
        // ========================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "       QUANTUM-INSPIRED GA"
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
        // 10. RUN QIGA
        // ========================================

        optimizer.optimize(
                generations
        );


        // ========================================
        // 11. FINAL RESULT
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
                "             FINAL RESULT"
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
                "Best Route:"
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
                "Generations Without Improvement: "
                + optimizer
                        .getGenerationsWithoutImprovement()
        );

        System.out.println();

        System.out.println(
                "========================================"
        );
    }
}