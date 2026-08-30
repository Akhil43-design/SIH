package com.routeoptimizer;

public class Vehicle {

    private final String vehicleId;
    private final double capacity;
    private Location currentLocation;
    private final double fuelConsumptionRate;
    private final double costPerDistance;
    private boolean available;

    public Vehicle(
            String vehicleId,
            double capacity,
            Location currentLocation,
            double fuelConsumptionRate,
            double costPerDistance) {

        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID cannot be empty.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Vehicle capacity must be greater than 0: " + capacity);
        }
        if (fuelConsumptionRate < 0) {
            throw new IllegalArgumentException("Fuel consumption rate cannot be negative.");
        }
        if (costPerDistance < 0) {
            throw new IllegalArgumentException("Cost per distance cannot be negative.");
        }

        this.vehicleId = vehicleId;
        this.capacity = capacity;
        this.currentLocation = currentLocation;
        this.fuelConsumptionRate = fuelConsumptionRate;
        this.costPerDistance = costPerDistance;
        this.available = true;
    }

    public Vehicle(String vehicleId, double capacity, Location currentLocation) {
        this(vehicleId, capacity, currentLocation, 0.12, 10.0);
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public double getCapacity() {
        return capacity;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public double getFuelConsumptionRate() {
        return fuelConsumptionRate;
    }

    public double getCostPerDistance() {
        return costPerDistance;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Vehicle " + vehicleId + " [Cap: " + capacity + ", FuelRate: " + fuelConsumptionRate
                + ", CostRate: " + costPerDistance + "]";
    }
}
