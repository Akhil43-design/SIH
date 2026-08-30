package com.routeoptimizer;

public enum DeliveryPriority {

    LOW(1.0),
    MEDIUM(2.0),
    HIGH(3.0);

    private final double penaltyMultiplier;

    DeliveryPriority(double penaltyMultiplier) {
        this.penaltyMultiplier = penaltyMultiplier;
    }

    public double getPenaltyMultiplier() {
        return penaltyMultiplier;
    }
}
