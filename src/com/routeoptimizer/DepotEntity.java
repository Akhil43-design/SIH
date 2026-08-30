package com.routeoptimizer;

public class DepotEntity {

    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private boolean active = true;
    private long createdAt;
    private long updatedAt;

    public DepotEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public DepotEntity(String id, String name, double latitude, double longitude) {
        this();
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static DepotEntity fromDto(DepotDto dto) {
        if (dto == null) return null;
        DepotEntity entity = new DepotEntity(
                dto.getId(),
                dto.getName() != null ? dto.getName() : dto.getId(),
                dto.getLatitude() != null ? dto.getLatitude() : 0.0,
                dto.getLongitude() != null ? dto.getLongitude() : 0.0
        );
        return entity;
    }

    public DepotDto toDto() {
        return new DepotDto(id, name, latitude, longitude);
    }

    public Location toDomain() {
        if (latitude != 0.0 || longitude != 0.0) {
            return new GeoLocation(id, name, latitude, longitude);
        }
        return new Location(id, name);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
