package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteGenerator {

    public static List<Location> generateRandomOrder(
            List<Location> customers) {

        List<Location> route = new ArrayList<>(customers);

        Collections.shuffle(route);

        return route;
    }
}