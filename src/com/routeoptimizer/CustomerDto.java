package com.routeoptimizer;

public class CustomerDto {

    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double demand;
    private String priority; // HIGH, MEDIUM, LOW
    private Double serviceTime;
    private Double earliestTime;
    private Double latestTime;

    public CustomerDto() {
    }

    public CustomerDto(String id, String name, Double latitude, Double longitude, Double demand,
                       String priority, Double serviceTime, Double earliestTime, Double latestTime) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.demand = demand;
        this.priority = priority;
        this.serviceTime = serviceTime;
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
    }

    public void validate() {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Customer ID must not be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Customer name must not be empty.");
        }
        if (demand == null || demand <= 0) {
            throw new ValidationException("Customer demand must be strictly positive (got: " + demand + ").");
        }
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            throw new ValidationException("Invalid latitude: " + latitude + " (must be between -90 and +90).");
        }
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            throw new ValidationException("Invalid longitude: " + longitude + " (must be between -180 and +180).");
        }
        if (earliestTime != null && latestTime != null && earliestTime > latestTime) {
            throw new ValidationException("Earliest delivery time (" + earliestTime + ") cannot be greater than latest time (" + latestTime + ").");
        }
    }

    public Customer toDomain() {
        validate();
        DeliveryPriority dp = DeliveryPriority.MEDIUM;
        if (priority != null) {
            try {
                dp = DeliveryPriority.valueOf(priority.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        double st = serviceTime != null ? serviceTime : 5.0;
        double et = earliestTime != null ? earliestTime : 0.0;
        double lt = latestTime != null ? latestTime : 1440.0;

        if (latitude != null && longitude != null) {
            return new GeoCustomer(id, name, latitude, longitude, demand, dp, st, et, lt);
        }
        return new Customer(id, name, demand, dp, st, et, lt);
    }

    public static CustomerDto fromDomain(Customer customer) {
        if (customer == null) return null;
        Double lat = null, lon = null;
        if (customer instanceof GeoCustomer) {
            GeoCustomer gc = (GeoCustomer) customer;
            lat = gc.getLatitude();
            lon = gc.getLongitude();
        }
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                lat,
                lon,
                customer.getDemand(),
                customer.getPriority().name(),
                customer.getServiceTime(),
                customer.getEarliestDeliveryTime(),
                customer.getLatestDeliveryTime()
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Double getDemand() { return demand; }
    public void setDemand(Double demand) { this.demand = demand; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Double getServiceTime() { return serviceTime; }
    public void setServiceTime(Double serviceTime) { this.serviceTime = serviceTime; }
    public Double getEarliestTime() { return earliestTime; }
    public void setEarliestTime(Double earliestTime) { this.earliestTime = earliestTime; }
    public Double getLatestTime() { return latestTime; }
    public void setLatestTime(Double latestTime) { this.latestTime = latestTime; }
}
