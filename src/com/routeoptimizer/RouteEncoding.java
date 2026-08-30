package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class RouteEncoding {

    public static List<Location> decode(
            List<Integer> bits,
            List<Location> customers) {

        if (bits.size() != customers.size()) {
            throw new IllegalArgumentException(
                    "Number of bits must match number of customers."
            );
        }

        List<Location> remaining =
                new ArrayList<>(customers);

        List<Location> route =
                new ArrayList<>();

        // First select customers whose bit is 1
        for (int i = 0; i < bits.size(); i++) {

            if (bits.get(i) == 1) {
                route.add(customers.get(i));
                remaining.remove(customers.get(i));
            }
        }

        // Then add customers whose bit is 0
        route.addAll(remaining);

        return route;
    }
}