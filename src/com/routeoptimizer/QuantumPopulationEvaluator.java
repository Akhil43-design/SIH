package com.routeoptimizer;

import java.util.List;

public class QuantumPopulationEvaluator {

    private final Location warehouse;
    private final RoadNetwork network;
    private final FitnessFunction fitnessFunction;

    public QuantumPopulationEvaluator(
            Location warehouse,
            RoadNetwork network,
            FitnessFunction fitnessFunction) {

        this.warehouse = warehouse;
        this.network = network;
        this.fitnessFunction = fitnessFunction;
    }

    public Route buildRoute(
            List<PositionQBit> individual,
            List<Location> customers) {

        List<Location> customerOrder =
                QuantumPositionRouteGenerator
                        .generateRoute(
                                individual,
                                customers
                        );

        return RouteBuilder.buildRoute(
                warehouse,
                customerOrder,
                network
        );
    }

    /*
     * Build a route from an already generated
     * customer order.
     *
     * This prevents measuring the QBits twice.
     */
    public Route buildRouteFromOrder(
            List<Location> customerOrder) {

        return RouteBuilder.buildRoute(
                warehouse,
                customerOrder,
                network
        );
    }

    public double calculateCost(
            List<PositionQBit> individual,
            List<Location> customers) {

        Route route =
                buildRoute(
                        individual,
                        customers
                );

        return fitnessFunction.calculateCost(
                route
        );
    }

    public double calculateCost(
            Route route) {

        return fitnessFunction.calculateCost(
                route
        );
    }

    public Location getWarehouse() {

        return warehouse;
    }

    public RoadNetwork getNetwork() {

        return network;
    }

    public FitnessFunction getFitnessFunction() {

        return fitnessFunction;
    }
}