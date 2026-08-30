package com.routeoptimizer;

public class TrafficModel {

    private TrafficCondition defaultCondition;

    public TrafficModel(TrafficCondition defaultCondition) {
        this.defaultCondition = defaultCondition != null ? defaultCondition : TrafficCondition.LOW;
    }

    public TrafficModel() {
        this(TrafficCondition.LOW);
    }

    public TrafficCondition getDefaultCondition() {
        return defaultCondition;
    }

    public void setDefaultCondition(TrafficCondition defaultCondition) {
        this.defaultCondition = defaultCondition;
    }

    public double calculateAdjustedTravelTime(Road road, TrafficCondition condition) {
        if (road == null) {
            return 0.0;
        }
        TrafficCondition effective = condition != null ? condition : defaultCondition;
        return road.getTravelTime() * effective.getMultiplier();
    }

    public double calculateAdjustedTravelTime(Road road) {
        return calculateAdjustedTravelTime(road, defaultCondition);
    }
}
