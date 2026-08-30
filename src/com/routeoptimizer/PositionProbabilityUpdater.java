package com.routeoptimizer;

import java.util.List;

public class PositionProbabilityUpdater {

    private final double learningRate;

    public PositionProbabilityUpdater(
            double learningRate) {

        if (learningRate <= 0 ||
                learningRate > 1) {

            throw new IllegalArgumentException(
                    "Learning rate must be between 0 and 1."
            );
        }

        this.learningRate =
                learningRate;
    }

    public void update(
            List<PositionQBit> positions,
            List<Location> bestRoute,
            List<Location> customers) {

        if (positions == null ||
                bestRoute == null ||
                customers == null) {

            throw new IllegalArgumentException(
                    "Arguments cannot be null."
            );
        }

        if (positions.size()
                != bestRoute.size()) {

            throw new IllegalArgumentException(
                    "Position count and route size must match."
            );
        }

        for (int position = 0;
             position < positions.size();
             position++) {

            PositionQBit qbit =
                    positions.get(position);

            Location target =
                    bestRoute.get(position);

            int targetIndex =
                    customers.indexOf(target);

            if (targetIndex == -1) {

                throw new IllegalArgumentException(
                        "Best-route customer not found."
                );
            }

            double[] probabilities =
                    qbit.getProbabilities();

            /*
             * Move probability toward the
             * customer used by the best route.
             */
            for (int customer = 0;
                 customer < probabilities.length;
                 customer++) {

                double current =
                        probabilities[customer];

                if (customer == targetIndex) {

                    current =
                            current
                            + learningRate
                            * (1.0 - current);

                } else {

                    current =
                            current
                            * (1.0 - learningRate);
                }

                qbit.setProbability(
                        customer,
                        current
                );
            }

            normalize(qbit);
        }
    }

    private void normalize(
            PositionQBit qbit) {

        double[] probabilities =
                qbit.getProbabilities();

        double total = 0.0;

        for (double probability :
                probabilities) {

            total += probability;
        }

        if (total <= 0) {

            throw new IllegalStateException(
                    "Total probability must be positive."
            );
        }

        for (int i = 0;
             i < probabilities.length;
             i++) {

            qbit.setProbability(
                    i,
                    probabilities[i] / total
            );
        }
    }
}