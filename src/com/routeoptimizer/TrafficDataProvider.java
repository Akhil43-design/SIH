package com.routeoptimizer;

public interface TrafficDataProvider {

    TrafficMetrics getTraffic(Location origin, Location destination, long timestampMillis);

    double getAdjustedTravelTime(Road road, double baseTravelTimeMinutes, long timestampMillis);

    TrafficSourceMode getMode();

    String getSourceName();

    boolean isAvailable();
}
