package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class OptimizationResponse {

    public static class VehicleRouteResponse {
        private String vehicleId;
        private String depotId;
        private List<String> customerSequence = new ArrayList<>();
        private List<String> fullRouteLocationIds = new ArrayList<>();
        private Double totalDistanceKm;
        private Double totalTravelTimeMinutes;
        private Double totalFuelLiters;
        private Double totalCost;
        private Double totalDemand;
        private Double vehicleCapacity;
        private Double capacityViolation;
        private Integer timeViolations;

        public VehicleRouteResponse() {}

        public static VehicleRouteResponse fromDomain(VehicleRoute vr) {
            if (vr == null) return null;
            VehicleRouteResponse res = new VehicleRouteResponse();
            res.vehicleId = vr.getVehicle().getVehicleId();
            res.depotId = vr.getDepot().getId();
            res.totalDistanceKm = vr.getTotalDistance();
            res.totalTravelTimeMinutes = vr.getTotalTravelTime();
            res.totalFuelLiters = vr.getTotalFuel();
            res.totalCost = vr.getTotalCost();
            res.totalDemand = vr.getTotalDemand();
            res.vehicleCapacity = vr.getVehicle().getCapacity();
            res.capacityViolation = vr.getCapacityViolation();
            res.timeViolations = vr.getTimeViolationCount();

            for (Customer c : vr.getCustomers()) {
                res.customerSequence.add(c.getId());
            }
            res.fullRouteLocationIds.add(vr.getDepot().getId());
            for (Customer c : vr.getCustomers()) {
                res.fullRouteLocationIds.add(c.getId());
            }
            res.fullRouteLocationIds.add(vr.getDepot().getId());
            return res;
        }

        public String getVehicleId() { return vehicleId; }
        public String getDepotId() { return depotId; }
        public List<String> getCustomerSequence() { return customerSequence; }
        public List<String> getFullRouteLocationIds() { return fullRouteLocationIds; }
        public Double getTotalDistanceKm() { return totalDistanceKm; }
        public Double getTotalTravelTimeMinutes() { return totalTravelTimeMinutes; }
        public Double getTotalFuelLiters() { return totalFuelLiters; }
        public Double getTotalCost() { return totalCost; }
        public Double getTotalDemand() { return totalDemand; }
        public Double getVehicleCapacity() { return vehicleCapacity; }
        public Double getCapacityViolation() { return capacityViolation; }
        public Integer getTimeViolations() { return timeViolations; }
    }

    private String optimizationId;
    private String status; // QUEUED, RUNNING, COMPLETED, FAILED
    private Double optimizationScore;
    private Double totalDistanceKm;
    private Double totalTravelTimeMinutes;
    private Double totalWaitingTimeMinutes;
    private Double totalFuelLiters;
    private Double totalCost;
    private Integer totalCapacityViolations;
    private Integer totalTimeViolations;
    private Integer unassignedCount;
    private Integer duplicateCount;
    private String routingProvider;
    private String trafficSource;
    private Long runtimeMs;
    private List<VehicleRouteResponse> vehicleRoutes = new ArrayList<>();
    private String errorMessage;

    public OptimizationResponse() {}

    public static OptimizationResponse fromDomain(
            String id,
            FleetRoutePlan plan,
            String routingProvider,
            String trafficSource,
            long runtimeMs) {

        OptimizationResponse resp = new OptimizationResponse();
        resp.optimizationId = id;
        resp.status = "COMPLETED";
        resp.routingProvider = routingProvider != null ? routingProvider : "SYNTHETIC";
        resp.trafficSource = trafficSource != null ? trafficSource : "SIMULATED";
        resp.runtimeMs = runtimeMs;

        if (plan != null) {
            resp.optimizationScore = plan.getOverallFitness();
            resp.totalDistanceKm = plan.getTotalDistance();
            resp.totalTravelTimeMinutes = plan.getTotalTravelTime();
            resp.totalWaitingTimeMinutes = plan.getTotalWaitingTime();
            resp.totalFuelLiters = plan.getTotalFuel();
            resp.totalCost = plan.getTotalCost();
            resp.totalCapacityViolations = (int) plan.getTotalCapacityViolations();
            resp.totalTimeViolations = plan.getTotalTimeViolations();
            resp.unassignedCount = plan.getUnassignedCount();
            resp.duplicateCount = plan.getDuplicateCount();

            for (VehicleRoute vr : plan.getVehicleRoutes()) {
                resp.vehicleRoutes.add(VehicleRouteResponse.fromDomain(vr));
            }
        }
        return resp;
    }

    public static OptimizationResponse failed(String id, String errorMessage) {
        OptimizationResponse resp = new OptimizationResponse();
        resp.optimizationId = id;
        resp.status = "FAILED";
        resp.errorMessage = errorMessage;
        return resp;
    }

    public String getOptimizationId() { return optimizationId; }
    public void setOptimizationId(String optimizationId) { this.optimizationId = optimizationId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getOptimizationScore() { return optimizationScore; }
    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public Double getTotalTravelTimeMinutes() { return totalTravelTimeMinutes; }
    public Double getTotalWaitingTimeMinutes() { return totalWaitingTimeMinutes; }
    public Double getTotalFuelLiters() { return totalFuelLiters; }
    public Double getTotalCost() { return totalCost; }
    public Integer getTotalCapacityViolations() { return totalCapacityViolations; }
    public Integer getTotalTimeViolations() { return totalTimeViolations; }
    public Integer getUnassignedCount() { return unassignedCount; }
    public Integer getDuplicateCount() { return duplicateCount; }
    public String getRoutingProvider() { return routingProvider; }
    public String getTrafficSource() { return trafficSource; }
    public Long getRuntimeMs() { return runtimeMs; }
    public void setRuntimeMs(Long runtimeMs) { this.runtimeMs = runtimeMs; }
    public void setRuntimeMs(long runtimeMs) { this.runtimeMs = runtimeMs; }
    public List<VehicleRouteResponse> getVehicleRoutes() { return vehicleRoutes; }
    public String getErrorMessage() { return errorMessage; }
}
