package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class QuantumPopulation {

    private final List<List<QBit>> individuals;

    public QuantumPopulation(int populationSize, int numberOfQBits) {

        individuals = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {

            List<QBit> individual = new ArrayList<>();

            for (int j = 0; j < numberOfQBits; j++) {
                individual.add(new QBit());
            }

            individuals.add(individual);
        }
    }

    public List<List<QBit>> getIndividuals() {
        return individuals;
    }

    public int size() {
        return individuals.size();
    }

    public int getNumberOfQBits() {

        if (individuals.isEmpty()) {
            return 0;
        }

        return individuals.get(0).size();
    }
}