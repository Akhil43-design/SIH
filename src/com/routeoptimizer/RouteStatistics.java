package com.routeoptimizer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RouteStatistics {

    private final Set<String> uniqueRoutes =
            new HashSet<>();

    private double totalCost = 0.0;

    private double bestCost =
            Double.MAX_VALUE;

    private List<Location> bestRoute;

    private int count = 0;

    public void add(
            List<Location> route,
            double cost) {

        uniqueRoutes.add(
                createRouteKey(route)
        );

        totalCost += cost;

        count++;

        if (cost < bestCost) {

            bestCost = cost;

            bestRoute = route;
        }
    }

    private String createRouteKey(
            List<Location> route) {

        StringBuilder key =
                new StringBuilder();

        for (Location location : route) {

            key.append(
                    location.getId()
            );

            key.append("-");
        }

        return key.toString();
    }

    public int getUniqueRouteCount() {
        return uniqueRoutes.size();
    }

    public double getAverageCost() {

        if (count == 0) {
            return 0.0;
        }

        return totalCost / count;
    }

    public double getBestCost() {
        return bestCost;
    }

    public List<Location> getBestRoute() {
        return bestRoute;
    }
}