package com.routeoptimizer;

public class GeoCustomer extends Customer {

    private final double latitude;
    private final double longitude;

    public GeoCustomer(
            String id,
            String name,
            double latitude,
            double longitude,
            double demand,
            DeliveryPriority priority,
            double serviceTime,
            double earliestDeliveryTime,
            double latestDeliveryTime) {

        super(id, name, demand, priority, serviceTime, earliestDeliveryTime, latestDeliveryTime);

        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90: " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180: " + longitude);
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoCustomer(
            String id,
            String name,
            double latitude,
            double longitude,
            double demand) {

        this(id, name, latitude, longitude, demand, DeliveryPriority.MEDIUM, 5.0, 0.0, 500.0);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public GeoLocation toGeoLocation() {
        return new GeoLocation(getId(), getName(), latitude, longitude);
    }

    @Override
    public String toString() {
        return getId() + " (" + getName() + " [" + String.format("%.4f, %.4f", latitude, longitude)
                + "], D:" + getDemand() + ", P:" + getPriority() + ")";
    }
}
