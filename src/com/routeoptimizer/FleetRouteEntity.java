package com.routeoptimizer;

public class FleetRouteEntity {

    private String id;
    private String optimizationId;
    private String vehicleId;
    private String depotId;
    private double totalDistance;
    private double totalTravelTime;
    private double totalFuel;
    private double totalCost;
    private double routeScore;
    private double totalDemand;
    private double capacityViolation;
    private int timeViolations;
    private double lateness;
    private double waitingTime;

    public FleetRouteEntity() {}

    public static FleetRouteEntity fromDomain(String optimizationId, VehicleRoute vr) {
        if (vr == null) return null;
        FleetRouteEntity entity = new FleetRouteEntity();
        entity.id = optimizationId + "-" + vr.getVehicle().getVehicleId();
        entity.optimizationId = optimizationId;
        entity.vehicleId = vr.getVehicle().getVehicleId();
        entity.depotId = vr.getDepot().getId();
        entity.totalDistance = vr.getTotalDistance();
        entity.totalTravelTime = vr.getTotalTravelTime();
        entity.totalFuel = vr.getTotalFuel();
        entity.totalCost = vr.getTotalCost();
        entity.routeScore = vr.getTotalCost(); // Route cost / score
        entity.totalDemand = vr.getTotalDemand();
        entity.capacityViolation = vr.getCapacityViolation();
        entity.timeViolations = vr.getTimeViolationCount();
        entity.lateness = vr.getTotalLateness();
        entity.waitingTime = vr.getTotalWaitingTime();
        return entity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOptimizationId() { return optimizationId; }
    public void setOptimizationId(String optimizationId) { this.optimizationId = optimizationId; }
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public String getDepotId() { return depotId; }
    public void setDepotId(String depotId) { this.depotId = depotId; }
    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }
    public double getTotalTravelTime() { return totalTravelTime; }
    public void setTotalTravelTime(double totalTravelTime) { this.totalTravelTime = totalTravelTime; }
    public double getTotalFuel() { return totalFuel; }
    public void setTotalFuel(double totalFuel) { this.totalFuel = totalFuel; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public double getRouteScore() { return routeScore; }
    public void setRouteScore(double routeScore) { this.routeScore = routeScore; }
    public double getTotalDemand() { return totalDemand; }
    public void setTotalDemand(double totalDemand) { this.totalDemand = totalDemand; }
    public double getCapacityViolation() { return capacityViolation; }
    public void setCapacityViolation(double capacityViolation) { this.capacityViolation = capacityViolation; }
    public int getTimeViolations() { return timeViolations; }
    public void setTimeViolations(int timeViolations) { this.timeViolations = timeViolations; }
    public double getLateness() { return lateness; }
    public void setLateness(double lateness) { this.lateness = lateness; }
    public double getWaitingTime() { return waitingTime; }
    public void setWaitingTime(double waitingTime) { this.waitingTime = waitingTime; }
}
