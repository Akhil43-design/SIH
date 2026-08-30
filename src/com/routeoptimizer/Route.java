package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private final List<Road> roads;

    public Route() {
        this.roads = new ArrayList<>();
    }

    public void addRoad(Road road) {
        roads.add(road);
    }

    public List<Road> getRoads() {
        return roads;
    }

    public double getTotalDistance() {

        double total = 0;

        for (Road road : roads) {
            total += road.getDistance();
        }

        return total;
    }

    public double getTotalTravelTime() {

        double total = 0;

        for (Road road : roads) {
            total += road.getTravelTime();
        }

        return total;
    }

    public double getTotalFuelConsumption() {

        double total = 0;

        for (Road road : roads) {
            total += road.getFuelConsumption();
        }

        return total;
    }

    public int getTotalTraffic() {

        int total = 0;

        for (Road road : roads) {
            total += road.getTrafficLevel();
        }

        return total;
    }

    @Override
    public String toString() {

        StringBuilder route = new StringBuilder();

        if (!roads.isEmpty()) {
            route.append(roads.get(0).getFrom().getId());

            for (Road road : roads) {
                route.append(" -> ");
                route.append(road.getTo().getId());
            }
        }

        return route.toString();
    }
}