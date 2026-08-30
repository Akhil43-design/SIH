package com.routeoptimizer;

public class SimulatedTrafficProvider implements TrafficDataProvider {

    private final TimeDependentTrafficModel model;

    public SimulatedTrafficProvider(TimeDependentTrafficModel model) {
        this.model = model != null ? model : new TimeDependentTrafficModel();
    }

    public SimulatedTrafficProvider() {
        this(new TimeDependentTrafficModel());
    }

    @Override
    public TrafficMetrics getTraffic(Location origin, Location destination, long timestampMillis) {
        double minutesIntoDay = (timestampMillis / (60 * 1000L)) % 1440.0;
        double mult = model.getTrafficMultiplier(null, minutesIntoDay);
        return TrafficMetrics.createSimulated(mult, 10.0, timestampMillis);
    }

    @Override
    public double getAdjustedTravelTime(Road road, double baseTravelTimeMinutes, long timestampMillis) {
        if (road == null) {
            return baseTravelTimeMinutes;
        }
        double minutesIntoDay = (timestampMillis / (60 * 1000L)) % 1440.0;
        return model.calculateAdjustedTravelTime(road, minutesIntoDay);
    }

    @Override
    public TrafficSourceMode getMode() {
        return TrafficSourceMode.SIMULATED;
    }

    @Override
    public String getSourceName() {
        return "SimulatedTimeDependentTrafficModel";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    public TimeDependentTrafficModel getModel() {
        return model;
    }
}
