package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class BruteForceRouteOptimizer {

    private final Location warehouse;
    private final RoadNetwork network;
    private final FitnessFunction fitnessFunction;

    private List<Location> bestRoute;
    private double bestCost = Double.MAX_VALUE;

    public BruteForceRouteOptimizer(
            Location warehouse,
            RoadNetwork network,
            FitnessFunction fitnessFunction) {

        this.warehouse = warehouse;
        this.network = network;
        this.fitnessFunction = fitnessFunction;
    }

    public void optimize(List<Location> customers) {

        bestRoute = null;
        bestCost = Double.MAX_VALUE;

        List<Location> remaining =
                new ArrayList<>(customers);

        List<Location> currentRoute =
                new ArrayList<>();

        generatePermutations(
                remaining,
                currentRoute
        );
    }

    private void generatePermutations(
            List<Location> remaining,
            List<Location> currentRoute) {

        if (remaining.isEmpty()) {

            Route route =
                    RouteBuilder.buildRoute(
                            warehouse,
                            currentRoute,
                            network
                    );

            double cost =
                    fitnessFunction.calculateCost(
                            route
                    );

            if (cost < bestCost) {

                bestCost = cost;

                bestRoute =
                        new ArrayList<>(currentRoute);
            }

            return;
        }

        for (int i = 0;
             i < remaining.size();
             i++) {

            Location selected =
                    remaining.remove(i);

            currentRoute.add(selected);

            generatePermutations(
                    remaining,
                    currentRoute
            );

            currentRoute.remove(
                    currentRoute.size() - 1
            );

            remaining.add(
                    i,
                    selected
            );
        }
    }

    public List<Location> getBestRoute() {
        return new ArrayList<>(bestRoute);
    }

    public double getBestCost() {
        return bestCost;
    }
}