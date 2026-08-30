package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class QuantumPositionPopulation {

    private final List<List<PositionQBit>> individuals;

    public QuantumPositionPopulation(
            int populationSize,
            int numberOfCustomers) {

        if (populationSize <= 0 || numberOfCustomers <= 0) {
            throw new IllegalArgumentException(
                    "Population size and customer count must be greater than 0."
            );
        }

        individuals = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {

            List<PositionQBit> individual = new ArrayList<>();

            for (int position = 0;
                 position < numberOfCustomers;
                 position++) {

                individual.add(
                        new PositionQBit(numberOfCustomers)
                );
            }

            individuals.add(individual);
        }
    }

    public List<List<PositionQBit>> getIndividuals() {
        return individuals;
    }

    public int size() {
        return individuals.size();
    }

    public int getNumberOfPositions() {

        if (individuals.isEmpty()) {
            return 0;
        }

        return individuals.get(0).size();
    }
}