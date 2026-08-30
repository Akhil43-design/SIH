package com.routeoptimizer;

public class OptimizationResultEntity {

    private String optimizationId;
    private double totalDistance;
    private double totalTravelTime;
    private double totalFuel;
    private double totalCost;
    private double optimizationScore;
    private int capacityViolations;
    private int timeViolations;
    private double lateness;
    private double waitingTime;
    private int unassignedCustomers;
    private int duplicateCustomers;
    private long runtimeMs;
    private long createdAt;

    public OptimizationResultEntity() {
        this.createdAt = System.currentTimeMillis();
    }

    public static OptimizationResultEntity fromDomain(String optimizationId, FleetRoutePlan plan, long runtimeMs) {
        OptimizationResultEntity entity = new OptimizationResultEntity();
        entity.optimizationId = optimizationId;
        entity.runtimeMs = runtimeMs;
        if (plan != null) {
            entity.totalDistance = plan.getTotalDistance();
            entity.totalTravelTime = plan.getTotalTravelTime();
            entity.totalFuel = plan.getTotalFuel();
            entity.totalCost = plan.getTotalCost();
            entity.optimizationScore = plan.getOverallFitness();
            entity.capacityViolations = (int) plan.getTotalCapacityViolations();
            entity.timeViolations = plan.getTotalTimeViolations();
            entity.lateness = plan.getTotalLateness();
            entity.waitingTime = plan.getTotalWaitingTime();
            entity.unassignedCustomers = plan.getUnassignedCount();
            entity.duplicateCustomers = plan.getDuplicateCount();
        }
        return entity;
    }

    public String getOptimizationId() { return optimizationId; }
    public void setOptimizationId(String optimizationId) { this.optimizationId = optimizationId; }
    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }
    public double getTotalTravelTime() { return totalTravelTime; }
    public void setTotalTravelTime(double totalTravelTime) { this.totalTravelTime = totalTravelTime; }
    public double getTotalFuel() { return totalFuel; }
    public void setTotalFuel(double totalFuel) { this.totalFuel = totalFuel; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public double getOptimizationScore() { return optimizationScore; }
    public void setOptimizationScore(double optimizationScore) { this.optimizationScore = optimizationScore; }
    public int getCapacityViolations() { return capacityViolations; }
    public void setCapacityViolations(int capacityViolations) { this.capacityViolations = capacityViolations; }
    public int getTimeViolations() { return timeViolations; }
    public void setTimeViolations(int timeViolations) { this.timeViolations = timeViolations; }
    public double getLateness() { return lateness; }
    public void setLateness(double lateness) { this.lateness = lateness; }
    public double getWaitingTime() { return waitingTime; }
    public void setWaitingTime(double waitingTime) { this.waitingTime = waitingTime; }
    public int getUnassignedCustomers() { return unassignedCustomers; }
    public void setUnassignedCustomers(int unassignedCustomers) { this.unassignedCustomers = unassignedCustomers; }
    public int getDuplicateCustomers() { return duplicateCustomers; }
    public void setDuplicateCustomers(int duplicateCustomers) { this.duplicateCustomers = duplicateCustomers; }
    public long getRuntimeMs() { return runtimeMs; }
    public void setRuntimeMs(long runtimeMs) { this.runtimeMs = runtimeMs; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
