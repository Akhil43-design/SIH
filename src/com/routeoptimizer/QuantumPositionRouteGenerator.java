package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuantumPositionRouteGenerator {

private static final Random RANDOM = new Random();

/*
 * Adaptive exploration rate.
 *
 * The optimizer can change this value during execution.
 *
 * Higher value:
 *     More random exploration.
 *
 * Lower value:
 *     More exploitation of learned probabilities.
 */
private static double explorationRate = 0.20;

/**
 * Set exploration rate.
 *
 * Valid range: 0.0 to 1.0
 */
public static void setExplorationRate(double rate) {

    if (rate < 0.0 || rate > 1.0) {
        throw new IllegalArgumentException(
                "Exploration rate must be between 0 and 1."
        );
    }

    explorationRate = rate;
}

/**
 * Get current exploration rate.
 */
public static double getExplorationRate() {

    return explorationRate;
}

/**
 * Generate a valid permutation of all customers.
 *
 * Each customer appears exactly once.
 */
public static List<Location> generateRoute(
        List<PositionQBit> positions,
        List<Location> customers) {

    if (positions == null) {
        throw new IllegalArgumentException(
                "Positions cannot be null."
        );
    }

    if (customers == null) {
        throw new IllegalArgumentException(
                "Customers cannot be null."
        );
    }

    if (positions.size() != customers.size()) {
        throw new IllegalArgumentException(
                "Number of positions must match number of customers."
        );
    }

    if (customers.isEmpty()) {
        return new ArrayList<>();
    }

    List<Location> remaining =
            new ArrayList<>(customers);

    List<Location> route =
            new ArrayList<>();

    /*
     * Select exactly one customer for every position.
     */
    for (PositionQBit position : positions) {

        Location selected;

        /*
         * =========================================
         * EXPLORATION
         * =========================================
         */
        if (RANDOM.nextDouble() < explorationRate) {

            int randomIndex =
                    RANDOM.nextInt(remaining.size());

            selected =
                    remaining.get(randomIndex);

        } else {

            /*
             * =========================================
             * EXPLOITATION
             * =========================================
             */
            selected =
                    selectUsingProbability(
                            position,
                            remaining,
                            customers
                    );
        }

        route.add(selected);

        /*
         * Prevent duplicate customers.
         */
        remaining.remove(selected);
    }

    return route;
}

/**
 * Select a remaining customer using the
 * probability distribution stored in the QBit.
 */
private static Location selectUsingProbability(
        PositionQBit position,
        List<Location> remaining,
        List<Location> customers) {

    double totalProbability = 0.0;

    /*
     * Only probabilities belonging to customers
     * that have not already been selected are used.
     */
    for (Location customer : remaining) {

        int index =
                customers.indexOf(customer);

        if (index >= 0) {

            double probability =
                    position.getProbability(index);

            if (Double.isFinite(probability)
                    && probability > 0.0) {

                totalProbability += probability;
            }
        }
    }

    /*
     * If the probability distribution is invalid,
     * use uniform random selection.
     */
    if (!Double.isFinite(totalProbability)
            || totalProbability <= 0.0) {

        return remaining.get(
                RANDOM.nextInt(remaining.size())
        );
    }

    /*
     * Roulette-wheel selection.
     */
    double randomValue =
            RANDOM.nextDouble()
            * totalProbability;

    double cumulative = 0.0;

    for (Location customer : remaining) {

        int index =
                customers.indexOf(customer);

        if (index < 0) {
            continue;
        }

        double probability =
                position.getProbability(index);

        if (!Double.isFinite(probability)
                || probability <= 0.0) {
            continue;
        }

        cumulative += probability;

        if (randomValue <= cumulative) {
            return customer;
        }
    }

    /*
     * Floating-point safety fallback.
     */
    return remaining.get(
            remaining.size() - 1
    );
}

}