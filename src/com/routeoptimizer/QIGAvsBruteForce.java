package com.routeoptimizer;

import java.util.Arrays;
import java.util.List;

public class QIGAvsBruteForce {

    public static void main(String[] args) {

        // ========================================
        // LOCATIONS
        // ========================================

        Location warehouse =
                new Location("W", "Warehouse");

        Location customerA =
                new Location("A", "Customer A");

        Location customerB =
                new Location("B", "Customer B");

        Location customerC =
                new Location("C", "Customer C");

        List<Location> customers =
                Arrays.asList(
                        customerA,
                        customerB,
                        customerC
                );


        // ========================================
        // ROAD NETWORK
        // ========================================

        RoadNetwork network =
                new RoadNetwork();

        // Warehouse -> Customers

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


        // Customer -> Customer

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


        // Customer -> Warehouse

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
        // FITNESS FUNCTION
        // ========================================

        FitnessFunction fitnessFunction =
                new FitnessFunction(
                        0.25,
                        0.30,
                        0.20,
                        0.25,
                        50.0,
                        100.0,
                        10.0,
                        12.0
                );


        // ========================================
        // QIGA EVALUATOR
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

        PositionProbabilityUpdater updater =
                new PositionProbabilityUpdater(
                        0.05
                );

        QuantumPositionRouteGenerator
                .setExplorationRate(0.20);


        // ========================================
        // CREATE QIGA
        // ========================================

        QIGAOptimizer optimizer =
                new QIGAOptimizer(
                        30,
                        customers,
                        evaluator,
                        updater
                );


        // ========================================
        // RUN QIGA
        // ========================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "        QIGA vs BRUTE FORCE"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Running Quantum-Inspired Genetic Algorithm..."
        );

        System.out.println();

        optimizer.optimize(50);


        double qigaCost =
                optimizer.getBestCost();

        List<Location> qigaRoute =
                optimizer.getBestRoute();


        // ========================================
        // RUN EXACT BRUTE FORCE
        // ========================================

        System.out.println();

        System.out.println(
                "Running Exact Brute Force..."
        );

        BruteForceRouteOptimizer bruteForce =
                new BruteForceRouteOptimizer(
                        warehouse,
                        network,
                        fitnessFunction
                );

        bruteForce.optimize(customers);


        double bruteForceCost =
                bruteForce.getBestCost();

        List<Location> bruteForceRoute =
                bruteForce.getBestRoute();


        // ========================================
        // COMPARE
        // ========================================

        double difference =
                Math.abs(
                        qigaCost
                                - bruteForceCost
                );


        double accuracy;

        if (bruteForceCost == 0.0) {

            accuracy = 100.0;

        } else {

            accuracy =
                    (1.0
                            - difference
                            / Math.abs(
                                    bruteForceCost
                            ))
                            * 100.0;

            if (accuracy < 0.0) {
                accuracy = 0.0;
            }
        }


        // ========================================
        // DISPLAY RESULTS
        // ========================================

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "           COMPARISON RESULT"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "QIGA Best Route:"
        );

        System.out.println(
                qigaRoute
        );

        System.out.println();

        System.out.println(
                "QIGA Best Cost: "
                        + qigaCost
        );

        System.out.println();

        System.out.println(
                "Brute Force Best Route:"
        );

        System.out.println(
                bruteForceRoute
        );

        System.out.println();

        System.out.println(
                "Brute Force Best Cost: "
                        + bruteForceCost
        );

        System.out.println();

        System.out.println(
                "Cost Difference: "
                        + difference
        );

        System.out.println();

        System.out.printf(
                "QIGA Accuracy: %.2f%%%n",
                accuracy
        );

        System.out.println();


        // ========================================
        // VALIDATION
        // ========================================

        if (difference < 0.0000001) {

            System.out.println(
                    "RESULT: QIGA MATCHES "
                            + "EXACT SOLUTION"
            );

            System.out.println(
                    "VALIDATION: PASSED"
            );

        } else {

            System.out.println(
                    "RESULT: QIGA DOES NOT "
                            + "MATCH EXACT SOLUTION"
            );

            System.out.println(
                    "VALIDATION: NEEDS IMPROVEMENT"
            );
        }


        System.out.println();

        System.out.println(
                "========================================"
        );
    }
}