package com.routeoptimizer;

import java.util.Random;

public class QBit {

    private double alpha;
    private double beta;

    private static final Random RANDOM = new Random();

    public QBit() {
        alpha = 1.0 / Math.sqrt(2);
        beta = 1.0 / Math.sqrt(2);
    }

    public int measure() {

        double probabilityOfOne = beta * beta;

        if (RANDOM.nextDouble() < probabilityOfOne) {
            return 1;
        }

        return 0;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public double getProbabilityOfZero() {
        return alpha * alpha;
    }

    public double getProbabilityOfOne() {
        return beta * beta;
    }

    public void setState(double alpha, double beta) {

        double length = Math.sqrt(
                alpha * alpha + beta * beta
        );

        this.alpha = alpha / length;
        this.beta = beta / length;
    }
}