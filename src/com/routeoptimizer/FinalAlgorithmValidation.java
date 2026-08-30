package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinalAlgorithmValidation {

    public static void main(String[] args) {

        // ========================================
        // START TIMER
        // ========================================

        long startTime =
                System.nanoTime();


        // ========================================
        // CREATE LOCATIONS
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
        // CREATE ROAD NETWORK
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

        int populationSize = 30;

        int generations = 50;

        double learningRate = 0.05;

        double explorationRate = 0.20;


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
        // RUN QIGA
        // ========================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "     FINAL QIGA ALGORITHM VALIDATION"
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
                "Initial Exploration Rate: "
                        + explorationRate
        );

        System.out.println();

        System.out.println(
                "Running QIGA..."
        );

        System.out.println();


        optimizer.optimize(
                generations
        );


        // ========================================
        // GET FINAL RESULT
        // ========================================

        List<Location> bestCustomerOrder =
                optimizer.getBestRoute();

        double bestCost =
                optimizer.getBestCost();


        // ========================================
        // BUILD COMPLETE ROUTE
        // ========================================

        Route bestCompleteRoute =
                evaluator.buildRouteFromOrder(
                        bestCustomerOrder
                );


        // ========================================
        // VALIDATION 1
        // ROUTE SIZE
        // ========================================

        boolean routeSizeValid =
                bestCustomerOrder != null
                        &&
                bestCustomerOrder.size()
                        == customers.size();


        // ========================================
        // VALIDATION 2
        // ALL CUSTOMERS INCLUDED
        // ========================================

        Set<Location> uniqueCustomers =
                new HashSet<>(
                        bestCustomerOrder
                );

        boolean allCustomersIncluded =
                uniqueCustomers.size()
                        == customers.size();


        // ========================================
        // VALIDATION 3
        // NO DUPLICATES
        // ========================================

        boolean noDuplicates =
                bestCustomerOrder.size()
                        == uniqueCustomers.size();


        // ========================================
        // VALIDATION 4
        // COST VALID
        // ========================================

        boolean costValid =
                Double.isFinite(bestCost)
                        &&
                bestCost >= 0.0;


        // ========================================
        // VALIDATION 5
        // COMPLETE ROUTE EXISTS
        // ========================================

        boolean completeRouteValid =
                bestCompleteRoute != null;


        // ========================================
        // BRUTE FORCE VERIFICATION
        // ========================================

        System.out.println();

        System.out.println(
                "----------------------------------------"
        );

        System.out.println(
                "RUNNING EXACT VERIFICATION"
        );

        System.out.println(
                "----------------------------------------"
        );


        BruteForceRouteOptimizer bruteForce =
                new BruteForceRouteOptimizer(
                        warehouse,
                        network,
                        fitnessFunction
                );


        bruteForce.optimize(
                customers
        );


        double exactCost =
                bruteForce.getBestCost();

        List<Location> exactRoute =
                bruteForce.getBestRoute();


        // ========================================
        // COST COMPARISON
        // ========================================

        double costDifference =
                Math.abs(
                        bestCost
                                - exactCost
                );


        double tolerance =
                exactCost * 0.10;


        boolean withinTolerance =
                costDifference
                        <= tolerance;


        // ========================================
        // ROUTE DISPLAY
        // ========================================

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "           FINAL RESULTS"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "QIGA Customer Order:"
        );

        System.out.println(
                bestCustomerOrder
        );

        System.out.println();

        System.out.println(
                "QIGA Complete Route:"
        );

        printCompleteRoute(
                bestCustomerOrder
        );

        System.out.println();

        System.out.println(
                "QIGA Best Cost: "
                        + bestCost
        );

        System.out.println();

        System.out.println(
                "Exact Customer Order:"
        );

        System.out.println(
                exactRoute
        );

        System.out.println();

        System.out.println(
                "Exact Best Cost: "
                        + exactCost
        );

        System.out.println();

        System.out.println(
                "Cost Difference: "
                        + costDifference
        );

        System.out.println();

        System.out.println(
                "Allowed 10% Difference: "
                        + tolerance
        );


        // ========================================
        // VALIDATION RESULTS
        // ========================================

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "       ALGORITHM VALIDATION"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Route Size Valid: "
                        + routeSizeValid
        );

        System.out.println(
                "All Customers Included: "
                        + allCustomersIncluded
        );

        System.out.println(
                "No Duplicate Customers: "
                        + noDuplicates
        );

        System.out.println(
                "Cost Valid: "
                        + costValid
        );

        System.out.println(
                "Complete Route Valid: "
                        + completeRouteValid
        );

        System.out.println(
                "Within 10% of Exact Solution: "
                        + withinTolerance
        );


        // ========================================
        // OVERALL VALIDATION
        // ========================================

        boolean validationPassed =
                routeSizeValid
                        &&
                allCustomersIncluded
                        &&
                noDuplicates
                        &&
                costValid
                        &&
                completeRouteValid
                        &&
                withinTolerance;


        // ========================================
        // RUNTIME
        // ========================================

        long endTime =
                System.nanoTime();

        double runtimeMs =
                (endTime - startTime)
                        / 1_000_000.0;


        System.out.println();

        System.out.println(
                "Runtime: "
                        + runtimeMs
                        + " ms"
        );

        System.out.println();


        // ========================================
        // FINAL STATUS
        // ========================================

        System.out.println(
                "========================================"
        );

        if (validationPassed) {

            System.out.println(
                    "FINAL ALGORITHM VALIDATION: PASSED"
            );

            System.out.println();

            System.out.println(
                    "QIGA algorithm is ready for"
            );

            System.out.println(
                    "the next project phase."
            );

        } else {

            System.out.println(
                    "FINAL ALGORITHM VALIDATION: FAILED"
            );

            System.out.println();

            System.out.println(
                    "Further algorithm improvements"
            );

            System.out.println(
                    "are required."
            );
        }

        System.out.println(
                "========================================"
        );
    }


    // ========================================
    // PRINT COMPLETE ROUTE
    // ========================================

    private static void printCompleteRoute(
            List<Location> customerOrder) {

        System.out.print("W");

        for (Location location :
                customerOrder) {

            System.out.print(
                    " -> "
                            + location.getId()
            );
        }

        System.out.println(
                " -> W"
        );
    }
}