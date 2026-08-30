package com.routeoptimizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OptimizationRequest {

    private List<CustomerDto> customers = new ArrayList<>();
    private List<VehicleDto> vehicles = new ArrayList<>();
    private List<DepotDto> depots = new ArrayList<>();
    private String routingMode = "SYNTHETIC";
    private String trafficMode = "SIMULATED";
    private Long seed = 42L;
    private Integer populationSize = 50;
    private Integer generations = 100;
    private Double learningRate = 0.05;
    private Double explorationRate = 0.20;

    public OptimizationRequest() {
    }

    public void validate() {
        if (customers == null || customers.isEmpty()) {
            throw new ValidationException("Customer list must not be empty.");
        }
        if (vehicles == null || vehicles.isEmpty()) {
            throw new ValidationException("Vehicle fleet must not be empty.");
        }
        if (depots == null || depots.isEmpty()) {
            throw new ValidationException("At least one depot must be defined.");
        }

        // Validate uniqueness of IDs
        Set<String> custIds = new HashSet<>();
        for (CustomerDto c : customers) {
            c.validate();
            if (!custIds.add(c.getId())) {
                throw new ValidationException("Duplicate customer ID found: " + c.getId());
            }
        }

        Set<String> vehIds = new HashSet<>();
        for (VehicleDto v : vehicles) {
            v.validate();
            if (!vehIds.add(v.getId())) {
                throw new ValidationException("Duplicate vehicle ID found: " + v.getId());
            }
        }

        Set<String> depIds = new HashSet<>();
        for (DepotDto d : depots) {
            d.validate();
            if (!depIds.add(d.getId())) {
                throw new ValidationException("Duplicate depot ID found: " + d.getId());
            }
        }

        // Validate that vehicle home depots exist in depot list if specified
        for (VehicleDto v : vehicles) {
            if (v.getDepotId() != null && !depIds.contains(v.getDepotId())) {
                throw new ValidationException("Vehicle " + v.getId() + " references unknown depot ID: " + v.getDepotId());
            }
        }

        if (populationSize != null && populationSize <= 0) {
            throw new ValidationException("Population size must be positive.");
        }
        if (generations != null && generations <= 0) {
            throw new ValidationException("Generations count must be positive.");
        }
    }

    public List<CustomerDto> getCustomers() { return customers; }
    public void setCustomers(List<CustomerDto> customers) { this.customers = customers; }
    public List<VehicleDto> getVehicles() { return vehicles; }
    public void setVehicles(List<VehicleDto> vehicles) { this.vehicles = vehicles; }
    public List<DepotDto> getDepots() { return depots; }
    public void setDepots(List<DepotDto> depots) { this.depots = depots; }
    public String getRoutingMode() { return routingMode; }
    public void setRoutingMode(String routingMode) { this.routingMode = routingMode; }
    public String getTrafficMode() { return trafficMode; }
    public void setTrafficMode(String trafficMode) { this.trafficMode = trafficMode; }
    public Long getSeed() { return seed; }
    public void setSeed(Long seed) { this.seed = seed; }
    public Integer getPopulationSize() { return populationSize; }
    public void setPopulationSize(Integer populationSize) { this.populationSize = populationSize; }
    public Integer getGenerations() { return generations; }
    public void setGenerations(Integer generations) { this.generations = generations; }
    public Double getLearningRate() { return learningRate; }
    public void setLearningRate(Double learningRate) { this.learningRate = learningRate; }
    public Double getExplorationRate() { return explorationRate; }
    public void setExplorationRate(Double explorationRate) { this.explorationRate = explorationRate; }
}
