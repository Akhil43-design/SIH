package com.routeoptimizer;

import java.util.List;

public class QuantumRouteGenerator {

    public static List<Location> generateRoute(
            List<QBit> individual,
            List<Location> customers) {

        // Measure the QBits
        List<Integer> bits =
                QuantumMeasurement.measureIndividual(individual);

        // Convert bits into a valid customer order
        return RouteEncoding.decode(
                bits,
                customers
        );
    }
}