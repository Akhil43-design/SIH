package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FleetCustomerManager {

    private final Map<String, Customer> customers;
    private final List<Vehicle> vehicles;
    private final Location depot;
    private final RoadNetwork network;
    private final TrafficModel trafficModel;
    private final FleetFitnessFunction fitnessFunction;

    private FleetRoutePlan currentPlan;

    public FleetCustomerManager(
            List<Customer> initialCustomers,
            List<Vehicle> vehicles,
            Location depot,
            RoadNetwork network,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction) {

        this.customers = new HashMap<>();
        if (initialCustomers != null) {
            for (Customer c : initialCustomers) {
                this.customers.put(c.getCustomerId(), c);
            }
        }
        this.vehicles = new ArrayList<>(vehicles);
        this.depot = depot;
        this.network = network;
        this.trafficModel = trafficModel != null ? trafficModel : new TrafficModel();
        this.fitnessFunction = fitnessFunction != null ? fitnessFunction : new FleetFitnessFunction();
    }

    public void addCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        customers.put(customer.getCustomerId(), customer);
    }

    public boolean removeCustomer(String customerId) {
        if (customerId == null) {
            return false;
        }
        return customers.remove(customerId) != null;
    }

    public void updateCustomer(Customer customer) {
        if (customer == null || !customers.containsKey(customer.getCustomerId())) {
            throw new IllegalArgumentException("Customer does not exist: " + (customer != null ? customer.getCustomerId() : "null"));
        }
        customers.put(customer.getCustomerId(), customer);
    }

    public FleetRoutePlan reoptimize(int populationSize, int generations, Long seed) {
        if (customers.isEmpty()) {
            throw new IllegalStateException("Cannot optimize with zero customers.");
        }

        List<Customer> customerList = new ArrayList<>(customers.values());
        MultiVehicleQIGAOptimizer optimizer = new MultiVehicleQIGAOptimizer(
                populationSize,
                customerList,
                vehicles,
                depot,
                network,
                trafficModel,
                fitnessFunction,
                0.05,
                0.20,
                seed
        );

        this.currentPlan = optimizer.optimize(generations);
        return this.currentPlan;
    }

    public FleetRoutePlan getCurrentPlan() {
        return currentPlan;
    }

    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(new ArrayList<>(customers.values()));
    }
}
