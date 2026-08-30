package com.routeoptimizer;

public enum TrafficCondition {

    LOW(1.00),
    MEDIUM(1.25),
    HIGH(1.60);

    private final double multiplier;

    TrafficCondition(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
