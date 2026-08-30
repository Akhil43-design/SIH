package com.routeoptimizer;

import java.util.List;

public class RouteEvaluator {

    private final RoadNetwork network;
    private final FitnessFunction fitnessFunction;
    private final Location warehouse;

    public RouteEvaluator(
            RoadNetwork network,
            FitnessFunction fitnessFunction,
            Location warehouse) {

        this.network = network;
        this.fitnessFunction = fitnessFunction;
        this.warehouse = warehouse;
    }

    public double evaluate(List<Location> customerOrder) {

        Route route = RouteBuilder.buildRoute(
                warehouse,
                customerOrder,
                network
        );

        return fitnessFunction.calculateCost(route);
    }

    public Route buildRoute(List<Location> customerOrder) {

        return RouteBuilder.buildRoute(
                warehouse,
                customerOrder,
                network
        );
    }
}