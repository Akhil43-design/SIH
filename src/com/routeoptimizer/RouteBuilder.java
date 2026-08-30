package com.routeoptimizer;

import java.util.List;

public class RouteBuilder {

    public static Route buildRoute(
            Location warehouse,
            List<Location> customerOrder,
            RoadNetwork network) {

        Route route = new Route();

        Location current = warehouse;

        // Travel from warehouse through all customers
        for (Location customer : customerOrder) {

            Road road = network.findRoad(current, customer);

            if (road == null) {
                throw new IllegalArgumentException(
                        "No road found from "
                        + current.getId()
                        + " to "
                        + customer.getId()
                );
            }

            route.addRoad(road);
            current = customer;
        }

        // Return to warehouse
        Road returnRoad = network.findRoad(current, warehouse);

        if (returnRoad == null) {
            throw new IllegalArgumentException(
                    "No road found from "
                    + current.getId()
                    + " to warehouse"
            );
        }

        route.addRoad(returnRoad);

        return route;
    }
}