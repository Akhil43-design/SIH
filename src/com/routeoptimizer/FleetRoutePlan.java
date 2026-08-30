package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FleetRoutePlan {

    private final Location depot;
    private final List<VehicleRoute> vehicleRoutes;
    private final FleetFitnessFunction fitnessFunction;

    private double totalDistance;
    private double totalTravelTime;
    private double totalWaitingTime;
    private double totalFuel;
    private double totalCost;
    private double totalCapacityViolations;
    private double totalLateness;
    private int totalTimeViolations;
    private int unassignedCount;
    private int duplicateCount;
    private double overallFitness;

    public FleetRoutePlan(
            Location depot,
            List<VehicleRoute> vehicleRoutes,
            List<Customer> allExpectedCustomers,
            FleetFitnessFunction fitnessFunction) {

        this.depot = depot;
        this.vehicleRoutes = vehicleRoutes != null ? new ArrayList<>(vehicleRoutes) : new ArrayList<>();
        this.fitnessFunction = fitnessFunction != null ? fitnessFunction : new FleetFitnessFunction();

        calculateFleetMetrics(allExpectedCustomers);
    }

    private void calculateFleetMetrics(List<Customer> allExpectedCustomers) {
        totalDistance = 0.0;
        totalTravelTime = 0.0;
        totalWaitingTime = 0.0;
        totalFuel = 0.0;
        totalCost = 0.0;
        totalCapacityViolations = 0.0;
        totalLateness = 0.0;
        totalTimeViolations = 0;

        Set<String> seenCustomers = new HashSet<>();
        duplicateCount = 0;

        for (VehicleRoute vr : vehicleRoutes) {
            totalDistance += vr.getTotalDistance();
            totalTravelTime += vr.getTotalTravelTime();
            totalWaitingTime += vr.getTotalWaitingTime();
            totalFuel += vr.getTotalFuel();
            totalCost += vr.getTotalCost();
            totalCapacityViolations += vr.getCapacityViolation();
            totalLateness += vr.getTotalLateness();
            totalTimeViolations += vr.getTimeViolationCount();

            for (Customer c : vr.getCustomers()) {
                if (!seenCustomers.add(c.getCustomerId())) {
                    duplicateCount++;
                }
            }
        }

        unassignedCount = 0;
        if (allExpectedCustomers != null) {
            for (Customer c : allExpectedCustomers) {
                if (!seenCustomers.contains(c.getCustomerId())) {
                    unassignedCount++;
                }
            }
        }

        overallFitness = fitnessFunction.calculateFitness(
                totalDistance,
                totalTravelTime,
                totalFuel,
                totalCost,
                totalCapacityViolations,
                totalLateness,
                unassignedCount + duplicateCount
        );
    }

    public Location getDepot() {
        return depot;
    }

    public List<VehicleRoute> getVehicleRoutes() {
        return Collections.unmodifiableList(vehicleRoutes);
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalTravelTime() {
        return totalTravelTime;
    }

    public double getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public double getTotalFuel() {
        return totalFuel;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getTotalCapacityViolations() {
        return totalCapacityViolations;
    }

    public double getTotalLateness() {
        return totalLateness;
    }

    public int getTotalTimeViolations() {
        return totalTimeViolations;
    }

    public int getUnassignedCount() {
        return unassignedCount;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }

    public double getOverallFitness() {
        return overallFitness;
    }

    public boolean isValid() {
        return totalCapacityViolations <= 0.0001
                && totalTimeViolations == 0
                && unassignedCount == 0
                && duplicateCount == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FleetRoutePlan [Fitness: ").append(String.format("%.4f", overallFitness))
                .append(", Dist: ").append(String.format("%.2f", totalDistance))
                .append(", Time: ").append(String.format("%.2f", totalTravelTime))
                .append(", CapViol: ").append(totalCapacityViolations)
                .append(", TimeViol: ").append(totalTimeViolations)
                .append("]\n");
        for (VehicleRoute vr : vehicleRoutes) {
            sb.append("  ").append(vr.toString()).append("\n");
        }
        return sb.toString();
    }
}
