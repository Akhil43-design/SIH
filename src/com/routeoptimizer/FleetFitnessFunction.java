package com.routeoptimizer;

public class FleetFitnessFunction {

    private final double distanceWeight;
    private final double timeWeight;
    private final double fuelWeight;
    private final double costWeight;

    private final double maxDistance;
    private final double maxTime;
    private final double maxFuel;
    private final double maxCost;

    private final double capacityPenaltyWeight;
    private final double timeWindowPenaltyWeight;

    public FleetFitnessFunction(
            double distanceWeight,
            double timeWeight,
            double fuelWeight,
            double costWeight,
            double maxDistance,
            double maxTime,
            double maxFuel,
            double maxCost,
            double capacityPenaltyWeight,
            double timeWindowPenaltyWeight) {

        this.distanceWeight = distanceWeight;
        this.timeWeight = timeWeight;
        this.fuelWeight = fuelWeight;
        this.costWeight = costWeight;

        this.maxDistance = maxDistance > 0 ? maxDistance : 500.0;
        this.maxTime = maxTime > 0 ? maxTime : 1000.0;
        this.maxFuel = maxFuel > 0 ? maxFuel : 100.0;
        this.maxCost = maxCost > 0 ? maxCost : 5000.0;

        this.capacityPenaltyWeight = capacityPenaltyWeight;
        this.timeWindowPenaltyWeight = timeWindowPenaltyWeight;
    }

    public FleetFitnessFunction() {
        this(0.25, 0.25, 0.20, 0.30, 500.0, 1000.0, 100.0, 5000.0, 50.0, 10.0);
    }

    public double calculateFitness(
            double distance,
            double time,
            double fuel,
            double cost,
            double capacityViolation,
            double timeLateness,
            int unassignedCount) {

        double normDist = distance / maxDistance;
        double normTime = time / maxTime;
        double normFuel = fuel / maxFuel;
        double normCost = cost / maxCost;

        double baseCost = (distanceWeight * normDist)
                + (timeWeight * normTime)
                + (fuelWeight * normFuel)
                + (costWeight * normCost);

        double penalties = (capacityViolation * capacityPenaltyWeight)
                + (timeLateness * timeWindowPenaltyWeight)
                + (unassignedCount * 500.0);

        return baseCost + penalties;
    }

    public double getDistanceWeight() {
        return distanceWeight;
    }

    public double getTimeWeight() {
        return timeWeight;
    }

    public double getFuelWeight() {
        return fuelWeight;
    }

    public double getCostWeight() {
        return costWeight;
    }
}
