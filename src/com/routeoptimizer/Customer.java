package com.routeoptimizer;

import java.util.Objects;

public class Customer extends Location {

    private final double demand;
    private final DeliveryPriority priority;
    private final double serviceTime;
    private final double earliestDeliveryTime;
    private final double latestDeliveryTime;

    public Customer(
            String id,
            String name,
            double demand,
            DeliveryPriority priority,
            double serviceTime,
            double earliestDeliveryTime,
            double latestDeliveryTime) {

        super(id, name);

        if (demand < 0) {
            throw new IllegalArgumentException("Demand cannot be negative: " + demand);
        }
        if (serviceTime < 0) {
            throw new IllegalArgumentException("Service time cannot be negative: " + serviceTime);
        }
        if (earliestDeliveryTime < 0 || latestDeliveryTime < earliestDeliveryTime) {
            throw new IllegalArgumentException("Invalid time window: ["
                    + earliestDeliveryTime + ", " + latestDeliveryTime + "]");
        }

        this.demand = demand;
        this.priority = priority != null ? priority : DeliveryPriority.MEDIUM;
        this.serviceTime = serviceTime;
        this.earliestDeliveryTime = earliestDeliveryTime;
        this.latestDeliveryTime = latestDeliveryTime;
    }

    public Customer(String id, String name, double demand) {
        this(id, name, demand, DeliveryPriority.MEDIUM, 5.0, 0.0, 500.0);
    }

    public String getCustomerId() {
        return getId();
    }

    public double getDemand() {
        return demand;
    }

    public DeliveryPriority getPriority() {
        return priority;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public double getEarliestDeliveryTime() {
        return earliestDeliveryTime;
    }

    public double getLatestDeliveryTime() {
        return latestDeliveryTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            if (o instanceof Location) {
                return getId().equals(((Location) o).getId());
            }
            return false;
        }
        Customer customer = (Customer) o;
        return getId().equals(customer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return getId() + " (" + getName() + ", D:" + demand + ", P:" + priority + ")";
    }
}
