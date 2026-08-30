package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VehicleState {

    private final Vehicle vehicle;
    private Location currentLocation;
    private final List<Customer> completedCustomers;
    private final List<Customer> remainingCustomers;
    private double currentTimeMinutes;
    private double currentLoad;

    public VehicleState(Vehicle vehicle, List<Customer> initialRoutePlan, Location startDepot) {
        this.vehicle = vehicle;
        this.currentLocation = startDepot != null ? startDepot : vehicle.getCurrentLocation();
        this.completedCustomers = new ArrayList<>();
        this.remainingCustomers = new ArrayList<>(initialRoutePlan != null ? initialRoutePlan : Collections.emptyList());
        this.currentTimeMinutes = 0.0;
        this.currentLoad = 0.0;
    }

    public void markCustomerCompleted(Customer customer, double serviceCompletionTimeMinutes) {
        if (customer != null && remainingCustomers.contains(customer)) {
            remainingCustomers.remove(customer);
            completedCustomers.add(customer);
            this.currentLocation = customer;
            this.currentTimeMinutes = serviceCompletionTimeMinutes;
            this.currentLoad += customer.getDemand();
        }
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public List<Customer> getCompletedCustomers() {
        return Collections.unmodifiableList(completedCustomers);
    }

    public List<Customer> getRemainingCustomers() {
        return Collections.unmodifiableList(remainingCustomers);
    }

    public void setRemainingCustomers(List<Customer> newRemaining) {
        this.remainingCustomers.clear();
        if (newRemaining != null) {
            this.remainingCustomers.addAll(newRemaining);
        }
    }

    public double getCurrentTimeMinutes() {
        return currentTimeMinutes;
    }

    public void setCurrentTimeMinutes(double currentTimeMinutes) {
        this.currentTimeMinutes = currentTimeMinutes;
    }

    public double getCurrentLoad() {
        return currentLoad;
    }

    public double getRemainingCapacity() {
        return Math.max(0.0, vehicle.getCapacity() - currentLoad);
    }

    public boolean hasRemainingWork() {
        return !remainingCustomers.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("VehicleState [%s at %s, Completed: %d, Remaining: %d, Load: %.1f/%.1f, Time: %.1f min]",
                vehicle.getVehicleId(), currentLocation.getId(), completedCustomers.size(),
                remainingCustomers.size(), currentLoad, vehicle.getCapacity(), currentTimeMinutes);
    }
}
