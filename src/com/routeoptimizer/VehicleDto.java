package com.routeoptimizer;

public class VehicleDto {

    private String id;
    private Double capacity;
    private String depotId;
    private Double fuelConsumptionRate;
    private Double fixedCost;

    public VehicleDto() {
    }

    public VehicleDto(String id, Double capacity, String depotId, Double fuelConsumptionRate, Double fixedCost) {
        this.id = id;
        this.capacity = capacity;
        this.depotId = depotId;
        this.fuelConsumptionRate = fuelConsumptionRate;
        this.fixedCost = fixedCost;
    }

    public VehicleDto(String id, Double capacity, String depotId) {
        this(id, capacity, depotId, 0.12, 10.0);
    }

    public void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Vehicle ID must not be empty.");
        }
        if (capacity == null || capacity <= 0) {
            throw new ValidationException("Vehicle capacity must be strictly positive (got: " + capacity + ").");
        }
        if (fuelConsumptionRate != null && fuelConsumptionRate < 0) {
            throw new ValidationException("Fuel consumption rate cannot be negative.");
        }
        if (fixedCost != null && fixedCost < 0) {
            throw new ValidationException("Fixed cost cannot be negative.");
        }
    }

    public Vehicle toDomain(Location depot) {
        validate();
        double fuelRate = fuelConsumptionRate != null ? fuelConsumptionRate : 0.12;
        double cost = fixedCost != null ? fixedCost : 10.0;
        return new Vehicle(id, capacity, depot, fuelRate, cost);
    }

    public static VehicleDto fromDomain(Vehicle v) {
        if (v == null) return null;
        String dId = v.getCurrentLocation() != null ? v.getCurrentLocation().getId() : null;
        return new VehicleDto(
                v.getVehicleId(),
                v.getCapacity(),
                dId,
                v.getFuelConsumptionRate(),
                v.getCostPerDistance()
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }
    public String getDepotId() { return depotId; }
    public void setDepotId(String depotId) { this.depotId = depotId; }
    public Double getFuelConsumptionRate() { return fuelConsumptionRate; }
    public void setFuelConsumptionRate(Double fuelConsumptionRate) { this.fuelConsumptionRate = fuelConsumptionRate; }
    public Double getFixedCost() { return fixedCost; }
    public void setFixedCost(Double fixedCost) { this.fixedCost = fixedCost; }
}
