package com.routeoptimizer;

public class FitnessFunction {

    // Weights
    private final double distanceWeight;
    private final double timeWeight;
    private final double fuelWeight;
    private final double trafficWeight;

    // Maximum values used for normalization
    private final double maxDistance;
    private final double maxTime;
    private final double maxFuel;
    private final double maxTraffic;

    public FitnessFunction(
            double distanceWeight,
            double timeWeight,
            double fuelWeight,
            double trafficWeight,
            double maxDistance,
            double maxTime,
            double maxFuel,
            double maxTraffic) {

        this.distanceWeight = distanceWeight;
        this.timeWeight = timeWeight;
        this.fuelWeight = fuelWeight;
        this.trafficWeight = trafficWeight;

        this.maxDistance = maxDistance;
        this.maxTime = maxTime;
        this.maxFuel = maxFuel;
        this.maxTraffic = maxTraffic;
    }

    public double calculateCost(Route route) {

        double distance = route.getTotalDistance();
        double time = route.getTotalTravelTime();
        double fuel = route.getTotalFuelConsumption();
        double traffic = route.getTotalTraffic();

        // Normalize values between 0 and 1
        double distanceScore = distance / maxDistance;
        double timeScore = time / maxTime;
        double fuelScore = fuel / maxFuel;
        double trafficScore = traffic / maxTraffic;

        // Calculate final cost
        return (distanceWeight * distanceScore)
                + (timeWeight * timeScore)
                + (fuelWeight * fuelScore)
                + (trafficWeight * trafficScore);
    }
}