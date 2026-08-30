package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuantumPermutationGenerator {

    private static final Random RANDOM = new Random();

    public static List<Location> generateRoute(
            List<PositionQBit> positions,
            List<Location> customers) {

        if (positions.size() != customers.size()) {
            throw new IllegalArgumentException(
                    "Number of positions must match number of customers."
            );
        }

        List<Location> remaining =
                new ArrayList<>(customers);

        List<Location> route =
                new ArrayList<>();

        for (PositionQBit position : positions) {

            int selectedIndex =
                    selectUnusedCustomer(
                            position,
                            remaining,
                            customers
                    );

            Location selectedCustomer =
                    customers.get(selectedIndex);

            route.add(selectedCustomer);
            remaining.remove(selectedCustomer);
        }

        return route;
    }

    private static int selectUnusedCustomer(
            PositionQBit position,
            List<Location> remaining,
            List<Location> customers) {

        List<Integer> availableIndexes =
                new ArrayList<>();

        for (int i = 0; i < customers.size(); i++) {

            if (remaining.contains(customers.get(i))) {
                availableIndexes.add(i);
            }
        }

        double randomValue = RANDOM.nextDouble();
        double cumulative = 0.0;

        for (int index : availableIndexes) {

            cumulative +=
                    position.getProbability(index);

            if (randomValue < cumulative) {
                return index;
            }
        }

        return availableIndexes.get(
                availableIndexes.size() - 1
        );
    }
}