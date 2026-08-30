package com.routeoptimizer;

public class RouteStopEntity {

    private String id;
    private String fleetRouteId;
    private String customerId;
    private int sequenceNum;
    private double arrivalTime;
    private double serviceStartTime;
    private double departureTime;
    private double waitingTime;
    private double lateness;
    private boolean completed = false;

    public RouteStopEntity() {}

    public RouteStopEntity(String fleetRouteId, String customerId, int sequenceNum,
                           double arrivalTime, double serviceStartTime, double departureTime,
                           double waitingTime, double lateness, boolean completed) {
        this.id = fleetRouteId + "-stop-" + sequenceNum;
        this.fleetRouteId = fleetRouteId;
        this.customerId = customerId;
        this.sequenceNum = sequenceNum;
        this.arrivalTime = arrivalTime;
        this.serviceStartTime = serviceStartTime;
        this.departureTime = departureTime;
        this.waitingTime = waitingTime;
        this.lateness = lateness;
        this.completed = completed;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFleetRouteId() { return fleetRouteId; }
    public void setFleetRouteId(String fleetRouteId) { this.fleetRouteId = fleetRouteId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public int getSequenceNum() { return sequenceNum; }
    public void setSequenceNum(int sequenceNum) { this.sequenceNum = sequenceNum; }
    public double getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(double arrivalTime) { this.arrivalTime = arrivalTime; }
    public double getServiceStartTime() { return serviceStartTime; }
    public void setServiceStartTime(double serviceStartTime) { this.serviceStartTime = serviceStartTime; }
    public double getDepartureTime() { return departureTime; }
    public void setDepartureTime(double departureTime) { this.departureTime = departureTime; }
    public double getWaitingTime() { return waitingTime; }
    public void setWaitingTime(double waitingTime) { this.waitingTime = waitingTime; }
    public double getLateness() { return lateness; }
    public void setLateness(double lateness) { this.lateness = lateness; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
