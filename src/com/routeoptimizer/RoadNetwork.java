package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class RoadNetwork {

    private final List<Road> roads;

    public RoadNetwork() {
        roads = new ArrayList<>();
    }

    public void addRoad(Road road) {
        roads.add(road);
    }

    public Road findRoad(Location from, Location to) {

        for (Road road : roads) {

            if (road.getFrom().getId().equals(from.getId())
                    && road.getTo().getId().equals(to.getId())) {

                return road;
            }
        }

        return null;
    }

    public List<Road> getRoads() {
        return roads;
    }
}