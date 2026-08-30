package com.routeoptimizer;

import java.util.Arrays;
import java.util.List;

public class WeightExperiment {

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
        // RUN WEIGHT EXPERIMENTS
        // ========================================

        runExperiment(
                "Balanced Weights",
                0.25,
                0.30,
                0.20,
                0.25,
                warehouse,
                network,
                customers
        );


        runExperiment(
                "Distance Priority",
                0.60,
                0.15,
                0.10,
                0.15,
                warehouse,
                network,
                customers
        );


        runExperiment(
                "Time Priority",
                0.15,
                0.60,
                0.10,
                0.15,
                warehouse,
                network,
                customers
        );


        runExperiment(
                "Fuel Priority",
                0.15,
                0.15,
                0.60,
                0.10,
                warehouse,
                network,
                customers
        );


        runExperiment(
                "Traffic Priority",
                0.15,
                0.15,
                0.10,
                0.60,
                warehouse,
                network,
                customers
        );
    }


    // ========================================
    // EXPERIMENT METHOD
    // ========================================

    private static void runExperiment(
            String experimentName,
            double distanceWeight,
            double timeWeight,
            double fuelWeight,
            double trafficWeight,
            Location warehouse,
            RoadNetwork network,
            List<Location> customers) {


        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                experimentName
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Distance Weight: "
                + distanceWeight
        );

        System.out.println(
                "Time Weight: "
                + timeWeight
        );

        System.out.println(
                "Fuel Weight: "
                + fuelWeight
        );

        System.out.println(
                "Traffic Weight: "
                + trafficWeight
        );


        // ========================================
        // FITNESS FUNCTION
        // ========================================

        FitnessFunction fitnessFunction =
                new FitnessFunction(
                        distanceWeight,
                        timeWeight,
                        fuelWeight,
                        trafficWeight,

                        50.0,
                        100.0,
                        10.0,
                        12.0
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
        // UPDATER
        // ========================================

        PositionProbabilityUpdater updater =
                new PositionProbabilityUpdater(
                        0.05
                );


        // ========================================
        // EXPLORATION
        // ========================================

        QuantumPositionRouteGenerator
                .setExplorationRate(
                        0.20
                );


        // ========================================
        // QIGA
        // ========================================

        QIGAOptimizer optimizer =
                new QIGAOptimizer(
                        30,
                        customers,
                        evaluator,
                        updater
                );


        // ========================================
        // OPTIMIZE
        // ========================================

        optimizer.optimize(50);


        // ========================================
        // RESULT
        // ========================================

        System.out.println();

        System.out.println(
                "Best Customer Order: "
                + optimizer.getBestRoute()
        );

        System.out.println(
                "Best Cost: "
                + optimizer.getBestCost()
        );

        System.out.println(
                "Generations Without Improvement: "
                + optimizer
                        .getGenerationsWithoutImprovement()
        );
    }
}