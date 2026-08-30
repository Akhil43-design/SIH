package com.routeoptimizer;

public class Road {

    private final Location from;
    private final Location to;
    private final double distance;
    private final double travelTime;
    private final double fuelConsumption;
    private final int trafficLevel;

    public Road(Location from,
                Location to,
                double distance,
                double travelTime,
                double fuelConsumption,
                int trafficLevel) {

        this.from = from;
        this.to = to;
        this.distance = distance;
        this.travelTime = travelTime;
        this.fuelConsumption = fuelConsumption;
        this.trafficLevel = trafficLevel;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public int getTrafficLevel() {
        return trafficLevel;
    }

    @Override
    public String toString() {
        return from.getId() + " -> " + to.getId()
                + " | Distance: " + distance + " km"
                + " | Time: " + travelTime + " min"
                + " | Fuel: " + fuelConsumption + " L"
                + " | Traffic: " + trafficLevel;
    }
}