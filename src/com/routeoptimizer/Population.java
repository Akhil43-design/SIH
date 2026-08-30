package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private final List<List<Location>> routes;

    public Population() {
        routes = new ArrayList<>();
    }

    public void addRoute(List<Location> route) {
        routes.add(route);
    }

    public List<List<Location>> getRoutes() {
        return routes;
    }

    public int size() {
        return routes.size();
    }

    public List<Location> getBestRoute(RouteEvaluator evaluator) {

        List<Location> bestRoute = null;
        double bestCost = Double.MAX_VALUE;

        for (List<Location> route : routes) {

            double cost = evaluator.evaluate(route);

            if (cost < bestCost) {
                bestCost = cost;
                bestRoute = route;
            }
        }

        return bestRoute;
    }
}