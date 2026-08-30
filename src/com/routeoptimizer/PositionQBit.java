package com.routeoptimizer;

import java.util.Random;

public class PositionQBit {

    private final double[] probabilities;

    private static final Random RANDOM = new Random();

    public PositionQBit(int numberOfCustomers) {

        if (numberOfCustomers <= 0) {
            throw new IllegalArgumentException(
                    "Number of customers must be greater than 0."
            );
        }

        probabilities = new double[numberOfCustomers];

        double probability =
                1.0 / numberOfCustomers;

        for (int i = 0; i < numberOfCustomers; i++) {
            probabilities[i] = probability;
        }
    }

    public int measure() {

        double randomValue = RANDOM.nextDouble();
        double cumulative = 0.0;

        for (int i = 0; i < probabilities.length; i++) {

            cumulative += probabilities[i];

            if (randomValue < cumulative) {
                return i;
            }
        }

        return probabilities.length - 1;
    }

    public double[] getProbabilities() {
        return probabilities.clone();
    }

    public double getProbability(int customerIndex) {
        return probabilities[customerIndex];
    }

    public void setProbability(
            int customerIndex,
            double probability) {

        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException(
                    "Probability must be between 0 and 1."
            );
        }

        probabilities[customerIndex] = probability;
    }
}