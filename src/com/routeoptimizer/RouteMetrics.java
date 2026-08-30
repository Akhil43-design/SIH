package com.routeoptimizer;

public class RouteMetrics {

    private final double distanceKm;
    private final double travelTimeMinutes;
    private final String geometry;

    public RouteMetrics(double distanceKm, double travelTimeMinutes, String geometry) {
        this.distanceKm = Math.max(0.001, distanceKm);
        this.travelTimeMinutes = Math.max(0.001, travelTimeMinutes);
        this.geometry = geometry != null ? geometry : "";
    }

    public RouteMetrics(double distanceKm, double travelTimeMinutes) {
        this(distanceKm, travelTimeMinutes, "");
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getTravelTimeMinutes() {
        return travelTimeMinutes;
    }

    public String getGeometry() {
        return geometry;
    }

    @Override
    public String toString() {
        return String.format("RouteMetrics [Distance: %.2f km, Time: %.2f min]", distanceKm, travelTimeMinutes);
    }
}
