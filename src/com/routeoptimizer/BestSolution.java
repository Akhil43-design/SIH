package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class BestSolution {

    private List<Location> route;
    private double cost;

    public BestSolution() {
        this.route = new ArrayList<>();
        this.cost = Double.MAX_VALUE;
    }

    public void update(
            List<Location> route,
            double cost) {

        if (cost < this.cost) {

            this.route =
                    new ArrayList<>(route);

            this.cost = cost;
        }
    }

    public List<Location> getRoute() {
        return new ArrayList<>(route);
    }

    public double getCost() {
        return cost;
    }

    public boolean exists() {
        return !route.isEmpty();
    }
}