package com.routeoptimizer;

public class HaversineRoutingProvider implements RoutingProvider {

    private final double averageSpeedKmh;

    public HaversineRoutingProvider(double averageSpeedKmh) {
        this.averageSpeedKmh = averageSpeedKmh > 0 ? averageSpeedKmh : 30.0;
    }

    public HaversineRoutingProvider() {
        this(30.0);
    }

    @Override
    public RouteMetrics getRoute(GeoLocation origin, GeoLocation destination) {
        double distKm = getDistance(origin, destination);
        double timeMin = getTravelTime(origin, destination);
        return new RouteMetrics(distKm, timeMin, "haversine_line");
    }

    @Override
    public double getDistance(GeoLocation origin, GeoLocation destination) {
        if (origin == null || destination == null) {
            return 0.0;
        }
        return Math.max(0.05, origin.haversineDistanceTo(destination));
    }

    @Override
    public double getTravelTime(GeoLocation origin, GeoLocation destination) {
        double distKm = getDistance(origin, destination);
        return (distKm / averageSpeedKmh) * 60.0; // minutes
    }

    @Override
    public String getProviderName() {
        return "HaversineGeometricProvider";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
