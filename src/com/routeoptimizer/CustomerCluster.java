package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class CustomerCluster {
    private String clusterId;
    private String city;
    private double centroidLatitude;
    private double centroidLongitude;
    private List<Customer> customers;
    private List<Vehicle> assignedVehicles;
    private double estimatedDemand;
    private GeoBoundingBox boundingBox;

    public CustomerCluster(String clusterId, String city) {
        this.clusterId = clusterId;
        this.city = city;
        this.customers = new ArrayList<>();
        this.assignedVehicles = new ArrayList<>();
        this.estimatedDemand = 0.0;
        this.boundingBox = new GeoBoundingBox();
    }

    public void addCustomer(Customer customer) {
        this.customers.add(customer);
        this.estimatedDemand += customer.getDemand();
        
        if (customer instanceof GeoCustomer) {
            GeoCustomer gc = (GeoCustomer) customer;
            this.boundingBox.expandToInclude(gc.getLatitude(), gc.getLongitude());
        }
    }

    public void calculateCentroid() {
        if (customers.isEmpty()) return;
        
        double sumLat = 0;
        double sumLon = 0;
        int geoCount = 0;
        
        for (Customer c : customers) {
            if (c instanceof GeoCustomer) {
                GeoCustomer gc = (GeoCustomer) c;
                sumLat += gc.getLatitude();
                sumLon += gc.getLongitude();
                geoCount++;
            }
        }
        
        if (geoCount > 0) {
            this.centroidLatitude = sumLat / geoCount;
            this.centroidLongitude = sumLon / geoCount;
        }
    }

    public int size() {
        return customers.size();
    }

    public double totalDemand() {
        return estimatedDemand;
    }

    public GeoLocation centroid() {
        return new GeoLocation(clusterId + "_centroid", centroidLatitude, centroidLongitude);
    }

    public boolean containsCustomer(Customer c) {
        return customers.contains(c);
    }

    public List<Customer> getCustomers() {
        return customers;
    }
    
    public String getClusterId() {
        return clusterId;
    }
    
    public void assignVehicle(Vehicle v) {
        this.assignedVehicles.add(v);
    }
    
    public List<Vehicle> getAssignedVehicles() {
        return assignedVehicles;
    }
    
    public GeoBoundingBox getBoundingBox() {
        return boundingBox;
    }

    public static class GeoBoundingBox {
        private double minLat = 90.0;
        private double maxLat = -90.0;
        private double minLon = 180.0;
        private double maxLon = -180.0;

        public void expandToInclude(double lat, double lon) {
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;
        }
        
        public double getMinLat() { return minLat; }
        public double getMaxLat() { return maxLat; }
        public double getMinLon() { return minLon; }
        public double getMaxLon() { return maxLon; }
    }
}
