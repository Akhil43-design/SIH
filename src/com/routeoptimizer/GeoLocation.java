package com.routeoptimizer;

public class GeoLocation extends Location {

    private final double latitude;
    private final double longitude;

    public GeoLocation(String id, String name, double latitude, double longitude) {
        super(id, name);

        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees: " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees: " + longitude);
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoLocation(String id, double latitude, double longitude) {
        this(id, id, latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double haversineDistanceTo(GeoLocation other) {
        if (other == null) {
            return 0.0;
        }

        final double EARTH_RADIUS_KM = 6371.0;
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    @Override
    public String toString() {
        return getId() + " (" + getName() + " [" + String.format("%.5f, %.5f", latitude, longitude) + "])";
    }
}
