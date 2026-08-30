package com.routeoptimizer;

public class TimeDependentTrafficModel extends TrafficModel {

    public TimeDependentTrafficModel() {
        super(TrafficCondition.LOW);
    }

    public double getTrafficMultiplier(Road road, double departureTimeMinutes) {
        // Normalize time of day within 24-hour cycle (1440 minutes)
        double timeOfDay = departureTimeMinutes % 1440.0;
        if (timeOfDay < 0) timeOfDay += 1440.0;

        double baseTimeMultiplier;

        // Morning Peak: 07:00 (420 min) to 09:30 (570 min)
        if (timeOfDay >= 420.0 && timeOfDay < 570.0) {
            double peakFactor = Math.sin((timeOfDay - 420.0) / 150.0 * Math.PI);
            baseTimeMultiplier = 1.25 + 0.35 * peakFactor; // up to 1.60x
        }
        // Daytime Normal: 09:30 to 16:30 (990 min)
        else if (timeOfDay >= 570.0 && timeOfDay < 990.0) {
            baseTimeMultiplier = 1.15;
        }
        // Evening Peak: 16:30 (990 min) to 19:30 (1170 min)
        else if (timeOfDay >= 990.0 && timeOfDay < 1170.0) {
            double peakFactor = Math.sin((timeOfDay - 990.0) / 180.0 * Math.PI);
            baseTimeMultiplier = 1.30 + 0.45 * peakFactor; // up to 1.75x
        }
        // Late Evening / Off-Peak Night: 19:30 to 07:00
        else {
            baseTimeMultiplier = 1.00;
        }

        // Modulate with road traffic level (Level 1, 2, 3)
        if (road != null && road.getTrafficLevel() > 1) {
            double congestionAmplifier = 1.0 + (road.getTrafficLevel() - 1) * 0.15;
            return baseTimeMultiplier * congestionAmplifier;
        }

        return baseTimeMultiplier;
    }

    @Override
    public double calculateAdjustedTravelTime(Road road, double departureTimeMinutes) {
        if (road == null) {
            return 0.0;
        }
        double multiplier = getTrafficMultiplier(road, departureTimeMinutes);
        return road.getTravelTime() * multiplier;
    }
}
