package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FleetManagementService {

    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();
    private final Map<String, Location> depots = new ConcurrentHashMap<>();

    public FleetManagementService() {
    }

    // --- Customers CRUD ---

    public Customer createCustomer(CustomerDto dto) {
        if (dto == null) {
            throw new ValidationException("Customer body must not be null.");
        }
        dto.validate();
        if (customers.containsKey(dto.getId())) {
            throw new ApiException(409, "CONFLICT", "Customer with ID " + dto.getId() + " already exists.");
        }
        Customer customer = dto.toDomain();
        customers.put(customer.getId(), customer);
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public Customer getCustomer(String id) {
        Customer c = customers.get(id);
        if (c == null) {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found.");
        }
        return c;
    }

    public Customer updateCustomer(String id, CustomerDto dto) {
        if (!customers.containsKey(id)) {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found.");
        }
        dto.setId(id);
        dto.validate();
        Customer updated = dto.toDomain();
        customers.put(id, updated);
        return updated;
    }

    public boolean deleteCustomer(String id) {
        if (!customers.containsKey(id)) {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found.");
        }
        return customers.remove(id) != null;
    }

    // --- Depots CRUD ---

    public Location createDepot(DepotDto dto) {
        if (dto == null) {
            throw new ValidationException("Depot body must not be null.");
        }
        dto.validate();
        if (depots.containsKey(dto.getId())) {
            throw new ApiException(409, "CONFLICT", "Depot with ID " + dto.getId() + " already exists.");
        }
        Location depot = dto.toDomain();
        depots.put(depot.getId(), depot);
        return depot;
    }

    public List<Location> getAllDepots() {
        return new ArrayList<>(depots.values());
    }

    public Location getDepot(String id) {
        Location d = depots.get(id);
        if (d == null) {
            throw new ResourceNotFoundException("Depot with ID '" + id + "' not found.");
        }
        return d;
    }

    public Location updateDepot(String id, DepotDto dto) {
        if (!depots.containsKey(id)) {
            throw new ResourceNotFoundException("Depot with ID '" + id + "' not found.");
        }
        dto.setId(id);
        dto.validate();
        Location updated = dto.toDomain();
        depots.put(id, updated);
        return updated;
    }

    public boolean deleteDepot(String id) {
        if (!depots.containsKey(id)) {
            throw new ResourceNotFoundException("Depot with ID '" + id + "' not found.");
        }
        return depots.remove(id) != null;
    }

    // --- Vehicles CRUD ---

    public Vehicle createVehicle(VehicleDto dto) {
        if (dto == null) {
            throw new ValidationException("Vehicle body must not be null.");
        }
        dto.validate();
        if (vehicles.containsKey(dto.getId())) {
            throw new ApiException(409, "CONFLICT", "Vehicle with ID " + dto.getId() + " already exists.");
        }
        Location homeDepot = null;
        if (dto.getDepotId() != null) {
            homeDepot = depots.get(dto.getDepotId());
            if (homeDepot == null) {
                throw new ValidationException("Depot with ID '" + dto.getDepotId() + "' does not exist.");
            }
        } else if (!depots.isEmpty()) {
            homeDepot = depots.values().iterator().next();
        } else {
            homeDepot = new Location("DEFAULT_DEPOT", "Default Depot");
        }

        Vehicle v = dto.toDomain(homeDepot);
        vehicles.put(v.getVehicleId(), v);
        return v;
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    public Vehicle getVehicle(String id) {
        Vehicle v = vehicles.get(id);
        if (v == null) {
            throw new ResourceNotFoundException("Vehicle with ID '" + id + "' not found.");
        }
        return v;
    }

    public Vehicle updateVehicle(String id, VehicleDto dto) {
        if (!vehicles.containsKey(id)) {
            throw new ResourceNotFoundException("Vehicle with ID '" + id + "' not found.");
        }
        dto.setId(id);
        dto.validate();

        Location homeDepot = null;
        if (dto.getDepotId() != null) {
            homeDepot = depots.get(dto.getDepotId());
            if (homeDepot == null) {
                throw new ValidationException("Depot with ID '" + dto.getDepotId() + "' does not exist.");
            }
        } else {
            homeDepot = vehicles.get(id).getCurrentLocation();
        }

        Vehicle updated = dto.toDomain(homeDepot);
        vehicles.put(id, updated);
        return updated;
    }

    public boolean deleteVehicle(String id) {
        if (!vehicles.containsKey(id)) {
            throw new ResourceNotFoundException("Vehicle with ID '" + id + "' not found.");
        }
        return vehicles.remove(id) != null;
    }

    public void clearAll() {
        customers.clear();
        vehicles.clear();
        depots.clear();
    }
}
