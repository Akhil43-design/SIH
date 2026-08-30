package com.routeoptimizer;

import java.util.List;

public class QuantumRouteEvaluator {

    private final Location warehouse;
    private final RoadNetwork network;
    private final FitnessFunction fitnessFunction;

    public QuantumRouteEvaluator(
            Location warehouse,
            RoadNetwork network,
            FitnessFunction fitnessFunction) {

        this.warehouse = warehouse;
        this.network = network;
        this.fitnessFunction = fitnessFunction;
    }

    public Route evaluate(
            List<QBit> individual,
            List<Location> customers) {

        // 1. Generate a customer order
        List<Location> customerOrder =
                QuantumRouteGenerator.generateRoute(
                        individual,
                        customers
                );

        // 2. Build the complete route
        Route route =
                RouteBuilder.buildRoute(
                        warehouse,
                        customerOrder,
                        network
                );

        // 3. Return the complete route
        return route;
    }

    public double calculateCost(
            List<QBit> individual,
            List<Location> customers) {

        Route route = evaluate(individual, customers);

        return fitnessFunction.calculateCost(route);
    }
}