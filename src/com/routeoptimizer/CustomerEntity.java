package com.routeoptimizer;

public class CustomerEntity {

    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private double demand;
    private String priority;
    private double serviceTime;
    private double earliestTime;
    private double latestTime;
    private boolean active = true;
    private boolean cancelled = false;
    private long createdAt;
    private long updatedAt;

    public CustomerEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public CustomerEntity(String id, String name, double latitude, double longitude, double demand,
                          String priority, double serviceTime, double earliestTime, double latestTime) {
        this();
        this.id = id;
        this.name = name != null ? name : id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.demand = demand;
        this.priority = priority != null ? priority : "MEDIUM";
        this.serviceTime = serviceTime;
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
    }

    public static CustomerEntity fromDto(CustomerDto dto) {
        if (dto == null) return null;
        return new CustomerEntity(
                dto.getId(),
                dto.getName() != null ? dto.getName() : dto.getId(),
                dto.getLatitude() != null ? dto.getLatitude() : 0.0,
                dto.getLongitude() != null ? dto.getLongitude() : 0.0,
                dto.getDemand() != null ? dto.getDemand() : 10.0,
                dto.getPriority() != null ? dto.getPriority() : "MEDIUM",
                dto.getServiceTime() != null ? dto.getServiceTime() : 5.0,
                dto.getEarliestTime() != null ? dto.getEarliestTime() : 0.0,
                dto.getLatestTime() != null ? dto.getLatestTime() : 1440.0
        );
    }

    public CustomerDto toDto() {
        return new CustomerDto(id, name, latitude, longitude, demand, priority, serviceTime, earliestTime, latestTime);
    }

    public Customer toDomain() {
        DeliveryPriority p = DeliveryPriority.MEDIUM;
        try {
            if (priority != null) p = DeliveryPriority.valueOf(priority.toUpperCase());
        } catch (Exception ignored) {}

        if (latitude != 0.0 || longitude != 0.0) {
            return new GeoCustomer(id, name, latitude, longitude, demand, p, serviceTime, earliestTime, latestTime);
        }
        return new Customer(id, name, demand, p, serviceTime, earliestTime, latestTime);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getDemand() { return demand; }
    public void setDemand(double demand) { this.demand = demand; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public double getServiceTime() { return serviceTime; }
    public void setServiceTime(double serviceTime) { this.serviceTime = serviceTime; }
    public double getEarliestTime() { return earliestTime; }
    public void setEarliestTime(double earliestTime) { this.earliestTime = earliestTime; }
    public double getLatestTime() { return latestTime; }
    public void setLatestTime(double latestTime) { this.latestTime = latestTime; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
