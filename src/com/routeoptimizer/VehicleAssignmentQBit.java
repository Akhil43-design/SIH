package com.routeoptimizer;

import java.util.Arrays;
import java.util.Random;

public class VehicleAssignmentQBit {

    private final int vehicleCount;
    private final double[] probabilities;

    public VehicleAssignmentQBit(int vehicleCount) {
        if (vehicleCount <= 0) {
            throw new IllegalArgumentException("Vehicle count must be positive.");
        }
        this.vehicleCount = vehicleCount;
        this.probabilities = new double[vehicleCount];
        Arrays.fill(this.probabilities, 1.0 / vehicleCount);
    }

    public int measure(Random random, double explorationRate) {
        if (random.nextDouble() < explorationRate) {
            return random.nextInt(vehicleCount);
        }

        double r = random.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < vehicleCount; i++) {
            cumulative += probabilities[i];
            if (r <= cumulative) {
                return i;
            }
        }
        return vehicleCount - 1;
    }

    public void update(int targetVehicle, double learningRate) {
        if (targetVehicle < 0 || targetVehicle >= vehicleCount) {
            return;
        }

        for (int i = 0; i < vehicleCount; i++) {
            if (i == targetVehicle) {
                probabilities[i] += learningRate * (1.0 - probabilities[i]);
            } else {
                probabilities[i] -= learningRate * probabilities[i];
            }
            if (probabilities[i] < 0.001) {
                probabilities[i] = 0.001;
            }
        }

        // Normalize
        double sum = 0.0;
        for (double p : probabilities) {
            sum += p;
        }
        for (int i = 0; i < vehicleCount; i++) {
            probabilities[i] /= sum;
        }
    }

    public double[] getProbabilities() {
        return Arrays.copyOf(probabilities, probabilities.length);
    }

    public int getVehicleCount() {
        return vehicleCount;
    }
}
