package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VehicleRoute {

    private final Vehicle vehicle;
    private final List<Customer> customers;
    private final Location depot;
    private final Route route;

    private double totalDemand;
    private double totalDistance;
    private double totalTravelTime;
    private double totalWaitingTime;
    private double totalServiceTime;
    private double totalLateness;
    private double totalFuel;
    private double totalCost;
    private double capacityViolation;
    private int timeViolationCount;

    public VehicleRoute(
            Vehicle vehicle,
            List<Customer> customers,
            Location depot,
            RoadNetwork network,
            TrafficModel trafficModel) {

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (depot == null) {
            throw new IllegalArgumentException("Depot cannot be null.");
        }

        this.vehicle = vehicle;
        this.customers = customers != null ? new ArrayList<>(customers) : new ArrayList<>();
        this.depot = depot;
        this.route = new Route();

        calculateRouteMetrics(network, trafficModel != null ? trafficModel : new TrafficModel());
    }

    private void calculateRouteMetrics(RoadNetwork network, TrafficModel trafficModel) {
        totalDemand = 0.0;
        totalDistance = 0.0;
        totalTravelTime = 0.0;
        totalWaitingTime = 0.0;
        totalServiceTime = 0.0;
        totalLateness = 0.0;
        totalFuel = 0.0;
        totalCost = 0.0;
        capacityViolation = 0.0;
        timeViolationCount = 0;

        if (customers.isEmpty()) {
            return;
        }

        for (Customer c : customers) {
            totalDemand += c.getDemand();
        }

        if (totalDemand > vehicle.getCapacity()) {
            capacityViolation = totalDemand - vehicle.getCapacity();
        }

        Location current = depot;
        double currentTime = 0.0; // departure from depot at time 0.0

        for (Customer customer : customers) {
            Road road = network.findRoad(current, customer);
            if (road == null) {
                throw new IllegalArgumentException("No road found from "
                        + current.getId() + " to " + customer.getId());
            }

            route.addRoad(road);
            totalDistance += road.getDistance();
            double travelTime = trafficModel.calculateAdjustedTravelTime(road);
            totalTravelTime += travelTime;
            totalFuel += road.getFuelConsumption() * (vehicle.getFuelConsumptionRate() / 0.12);

            currentTime += travelTime;

            // Time window checks
            if (currentTime < customer.getEarliestDeliveryTime()) {
                double wait = customer.getEarliestDeliveryTime() - currentTime;
                totalWaitingTime += wait;
                currentTime = customer.getEarliestDeliveryTime();
            } else if (currentTime > customer.getLatestDeliveryTime()) {
                double lateness = currentTime - customer.getLatestDeliveryTime();
                totalLateness += lateness * customer.getPriority().getPenaltyMultiplier();
                timeViolationCount++;
            }

            totalServiceTime += customer.getServiceTime();
            currentTime += customer.getServiceTime();

            current = customer;
        }

        // Return to depot
        Road returnRoad = network.findRoad(current, depot);
        if (returnRoad == null) {
            throw new IllegalArgumentException("No road found from "
                    + current.getId() + " to depot " + depot.getId());
        }

        route.addRoad(returnRoad);
        totalDistance += returnRoad.getDistance();
        double returnTravelTime = trafficModel.calculateAdjustedTravelTime(returnRoad);
        totalTravelTime += returnTravelTime;
        totalFuel += returnRoad.getFuelConsumption() * (vehicle.getFuelConsumptionRate() / 0.12);
        currentTime += returnTravelTime;

        // Operating cost = distance * costPerDistance + driver time cost (e.g. 0.5 per minute) + fuel cost (e.g. 1.5 per L)
        totalCost = (totalDistance * vehicle.getCostPerDistance())
                + (totalFuel * 1.5)
                + (totalTravelTime * 0.5);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public Location getDepot() {
        return depot;
    }

    public Route getRoute() {
        return route;
    }

    public double getTotalDemand() {
        return totalDemand;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalTravelTime() {
        return totalTravelTime;
    }

    public double getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public double getTotalServiceTime() {
        return totalServiceTime;
    }

    public double getTotalLateness() {
        return totalLateness;
    }

    public double getTotalFuel() {
        return totalFuel;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getCapacityViolation() {
        return capacityViolation;
    }

    public int getTimeViolationCount() {
        return timeViolationCount;
    }

    public boolean isCapacityValid() {
        return capacityViolation <= 0.0001;
    }

    public boolean isTimeWindowValid() {
        return timeViolationCount == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(vehicle.getVehicleId()).append(" [Demand: ").append(totalDemand).append("/").append(vehicle.getCapacity()).append("]: ");
        sb.append(depot.getId());
        for (Customer c : customers) {
            sb.append(" -> ").append(c.getId());
        }
        sb.append(" -> ").append(depot.getId());
        return sb.toString();
    }
}
