package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class QuantumMeasurement {

    public static List<Integer> measureIndividual(
            List<QBit> individual) {

        List<Integer> solution = new ArrayList<>();

        for (QBit qbit : individual) {
            solution.add(qbit.measure());
        }

        return solution;
    }

    public static List<List<Integer>> measurePopulation(
            QuantumPopulation population) {

        List<List<Integer>> solutions = new ArrayList<>();

        for (List<QBit> individual : population.getIndividuals()) {

            solutions.add(measureIndividual(individual));
        }

        return solutions;
    }
}