package com.routeoptimizer;

public class DepotDto {

    private String id;
    private String name;
    private Double latitude;
    private Double longitude;

    public DepotDto() {
    }

    public DepotDto(String id, String name, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Depot ID must not be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Depot name must not be empty.");
        }
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            throw new ValidationException("Invalid depot latitude: " + latitude);
        }
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            throw new ValidationException("Invalid depot longitude: " + longitude);
        }
    }

    public Location toDomain() {
        validate();
        if (latitude != null && longitude != null) {
            return new GeoLocation(id, name, latitude, longitude);
        }
        return new Location(id, name);
    }

    public static DepotDto fromDomain(Location loc) {
        if (loc == null) return null;
        Double lat = null, lon = null;
        if (loc instanceof GeoLocation) {
            GeoLocation gl = (GeoLocation) loc;
            lat = gl.getLatitude();
            lon = gl.getLongitude();
        }
        return new DepotDto(loc.getId(), loc.getName(), lat, lon);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
