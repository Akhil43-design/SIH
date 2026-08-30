package com.routeoptimizer;

public class VehicleEntity {

    private String id;
    private String name;
    private double capacity;
    private double fuelConsumptionRate;
    private double costPerDistance;
    private String depotId;
    private boolean active = true;
    private long createdAt;
    private long updatedAt;

    public VehicleEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public VehicleEntity(String id, String name, double capacity, double fuelConsumptionRate,
                         double costPerDistance, String depotId) {
        this();
        this.id = id;
        this.name = name != null ? name : id;
        this.capacity = capacity;
        this.fuelConsumptionRate = fuelConsumptionRate;
        this.costPerDistance = costPerDistance;
        this.depotId = depotId;
    }

    public static VehicleEntity fromDto(VehicleDto dto) {
        if (dto == null) return null;
        return new VehicleEntity(
                dto.getId(),
                dto.getId(),
                dto.getCapacity() != null ? dto.getCapacity() : 80.0,
                dto.getFuelConsumptionRate() != null ? dto.getFuelConsumptionRate() : 0.12,
                dto.getFixedCost() != null ? dto.getFixedCost() : 10.0,
                dto.getDepotId()
        );
    }

    public VehicleDto toDto() {
        return new VehicleDto(id, capacity, depotId, fuelConsumptionRate, costPerDistance);
    }

    public Vehicle toDomain(Location homeDepot) {
        return new Vehicle(id, capacity, homeDepot, fuelConsumptionRate, costPerDistance);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCapacity() { return capacity; }
    public void setCapacity(double capacity) { this.capacity = capacity; }
    public double getFuelConsumptionRate() { return fuelConsumptionRate; }
    public void setFuelConsumptionRate(double fuelConsumptionRate) { this.fuelConsumptionRate = fuelConsumptionRate; }
    public double getCostPerDistance() { return costPerDistance; }
    public void setCostPerDistance(double costPerDistance) { this.costPerDistance = costPerDistance; }
    public String getDepotId() { return depotId; }
    public void setDepotId(String depotId) { this.depotId = depotId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
