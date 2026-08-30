package com.routeoptimizer;

import java.util.List;

public class ProbabilityUpdater {

    private final double learningRate;

    public ProbabilityUpdater(double learningRate) {
        this.learningRate = learningRate;
    }

    public void update(
            List<QBit> individual,
            List<Integer> bestSolution) {

        if (individual.size() != bestSolution.size()) {
            throw new IllegalArgumentException(
                    "QBit count and solution size must match."
            );
        }

        for (int i = 0; i < individual.size(); i++) {

            QBit qbit = individual.get(i);

            int target = bestSolution.get(i);

            double alpha = qbit.getAlpha();
            double beta = qbit.getBeta();

            if (target == 0) {

                alpha = alpha + learningRate * beta;
                beta = beta - learningRate * alpha;

            } else {

                alpha = alpha - learningRate * beta;
                beta = beta + learningRate * alpha;
            }

            qbit.setState(alpha, beta);
        }
    }
}