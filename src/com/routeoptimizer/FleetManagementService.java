package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class FleetManagementService {

    private final DatabaseManager db;
    private final CustomerRepository customerRepo;
    private final VehicleRepository vehicleRepo;
    private final DepotRepository depotRepo;

    public FleetManagementService(DatabaseManager db) {
        this.db = db != null ? db : new DatabaseManager();
        this.customerRepo = new CustomerRepository(this.db);
        this.vehicleRepo = new VehicleRepository(this.db);
        this.depotRepo = new DepotRepository(this.db);
    }

    public FleetManagementService() {
        this(new DatabaseManager());
    }

    // --- Depots CRUD ---

    public Location createDepot(DepotDto dto) {
        if (dto == null) {
            throw new ValidationException("Depot body must not be null.");
        }
        dto.validate();
        if (depotRepo.existsById(dto.getId())) {
            throw new ApiException(409, "CONFLICT", "Depot with ID " + dto.getId() + " already exists.");
        }
        DepotEntity entity = DepotEntity.fromDto(dto);
        depotRepo.save(entity);
        return entity.toDomain();
    }

    public List<Location> getAllDepots() {
        List<Location> list = new ArrayList<>();
        for (DepotEntity d : depotRepo.findAll()) {
            list.add(d.toDomain());
        }
        return list;
    }

    public Location getDepot(String id) {
        DepotEntity d = depotRepo.findById(id);
        if (d == null) {
            throw new ResourceNotFoundException("Depot with ID '" + id + "' not found.");
        }
        return d.toDomain();
    }

    public Location updateDepot(String id, DepotDto dto) {
        if (!depotRepo.existsById(id)) {
            throw new ResourceNotFoundException("Depot with ID '" + id + "' not found.");
        }
        dto.setId(id);
        dto.validate();
        DepotEntity updated = DepotEntity.fromDto(dto);
        depotRepo.save(updated);
        return updated.toDomain();
    }

    public boolean deleteDepot(String id) {
        if (!depotRepo.existsById(id)) {
            throw new ResourceNotFoundException("Depot with ID '" + id + "' not found.");
        }
        return depotRepo.deleteById(id);
    }

    // --- Vehicles CRUD ---

    public Vehicle createVehicle(VehicleDto dto) {
        if (dto == null) {
            throw new ValidationException("Vehicle body must not be null.");
        }
        dto.validate();
        if (vehicleRepo.existsById(dto.getId())) {
            throw new ApiException(409, "CONFLICT", "Vehicle with ID " + dto.getId() + " already exists.");
        }
        Location homeDepot = null;
        if (dto.getDepotId() != null) {
            DepotEntity d = depotRepo.findById(dto.getDepotId());
            if (d == null) {
                throw new ValidationException("Depot with ID '" + dto.getDepotId() + "' does not exist.");
            }
            homeDepot = d.toDomain();
        } else {
            List<DepotEntity> allDepots = depotRepo.findAll();
            if (!allDepots.isEmpty()) {
                homeDepot = allDepots.get(0).toDomain();
                dto.setDepotId(homeDepot.getId());
            } else {
                homeDepot = new Location("DEFAULT_DEPOT", "Default Depot");
            }
        }

        VehicleEntity entity = VehicleEntity.fromDto(dto);
        vehicleRepo.save(entity);
        return entity.toDomain(homeDepot);
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        for (VehicleEntity v : vehicleRepo.findAll()) {
            Location homeDepot = null;
            if (v.getDepotId() != null) {
                DepotEntity d = depotRepo.findById(v.getDepotId());
                if (d != null) homeDepot = d.toDomain();
            }
            if (homeDepot == null) {
                homeDepot = new Location("DEFAULT_DEPOT", "Default Depot");
            }
            list.add(v.toDomain(homeDepot));
        }
        return list;
    }

    public Vehicle getVehicle(String id) {
        VehicleEntity v = vehicleRepo.findById(id);
        if (v == null) {
            throw new ResourceNotFoundException("Vehicle with ID '" + id + "' not found.");
        }
        Location homeDepot = null;
        if (v.getDepotId() != null) {
            DepotEntity d = depotRepo.findById(v.getDepotId());
            if (d != null) homeDepot = d.toDomain();
        }
        if (homeDepot == null) {
            homeDepot = new Location("DEFAULT_DEPOT", "Default Depot");
        }
        return v.toDomain(homeDepot);
    }

    public Vehicle updateVehicle(String id, VehicleDto dto) {
        if (!vehicleRepo.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle with ID '" + id + "' not found.");
        }
        dto.setId(id);
        dto.validate();

        Location homeDepot = null;
        if (dto.getDepotId() != null) {
            DepotEntity d = depotRepo.findById(dto.getDepotId());
            if (d == null) {
                throw new ValidationException("Depot with ID '" + dto.getDepotId() + "' does not exist.");
            }
            homeDepot = d.toDomain();
        } else {
            homeDepot = getVehicle(id).getCurrentLocation();
        }

        VehicleEntity updated = VehicleEntity.fromDto(dto);
        vehicleRepo.save(updated);
        return updated.toDomain(homeDepot);
    }

    public boolean deleteVehicle(String id) {
        if (!vehicleRepo.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle with ID '" + id + "' not found.");
        }
        return vehicleRepo.deleteById(id);
    }

    // --- Customers CRUD ---

    public Customer createCustomer(CustomerDto dto) {
        if (dto == null) {
            throw new ValidationException("Customer body must not be null.");
        }
        dto.validate();
        if (customerRepo.existsById(dto.getId())) {
            throw new ApiException(409, "CONFLICT", "Customer with ID " + dto.getId() + " already exists.");
        }
        CustomerEntity entity = CustomerEntity.fromDto(dto);
        customerRepo.save(entity);
        return entity.toDomain();
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        for (CustomerEntity c : customerRepo.findAll()) {
            list.add(c.toDomain());
        }
        return list;
    }

    public List<Customer> getAllActiveOptimizationCustomers() {
        List<Customer> list = new ArrayList<>();
        for (CustomerEntity c : customerRepo.findAllActiveForOptimization()) {
            list.add(c.toDomain());
        }
        return list;
    }

    public Customer getCustomer(String id) {
        CustomerEntity c = customerRepo.findById(id);
        if (c == null) {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found.");
        }
        return c.toDomain();
    }

    public Customer updateCustomer(String id, CustomerDto dto) {
        if (!customerRepo.existsById(id)) {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found.");
        }
        dto.setId(id);
        dto.validate();
        CustomerEntity updated = CustomerEntity.fromDto(dto);
        customerRepo.save(updated);
        return updated.toDomain();
    }

    public boolean cancelCustomer(String id) {
        CustomerEntity c = customerRepo.findById(id);
        if (c == null) {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found.");
        }
        c.setCancelled(true);
        customerRepo.save(c);
        return true;
    }

    public boolean deleteCustomer(String id) {
        if (!customerRepo.existsById(id)) {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found.");
        }
        return customerRepo.deleteById(id);
    }

    public IndianCityDatasets.CityDataset loadCityDataset(String cityId) {
        IndianCityDatasets.CityDataset cityDataset = IndianCityDatasets.getCityDataset(cityId);
        db.beginTransaction();
        try {
            // Clear in-memory maps under lock
            db.customers.clear();
            db.vehicles.clear();
            db.depots.clear();

            for (DepotDto d : cityDataset.getDepots()) {
                depotRepo.save(DepotEntity.fromDto(d));
            }

            for (VehicleDto v : cityDataset.getVehicles()) {
                vehicleRepo.save(VehicleEntity.fromDto(v));
            }

            for (CustomerDto c : cityDataset.getCustomers()) {
                customerRepo.save(CustomerEntity.fromDto(c));
            }

            db.commit();
            return cityDataset;
        } catch (Exception e) {
            db.rollback();
            throw new ApiException(500, "DATASET_LOAD_FAILED", "Failed to load city dataset: " + e.getMessage());
        }
    }

    public void clearAll() {
        db.clearAll();
    }

    public DatabaseManager getDatabaseManager() {
        return db;
    }

    public CustomerRepository getCustomerRepo() { return customerRepo; }
    public VehicleRepository getVehicleRepo() { return vehicleRepo; }
    public DepotRepository getDepotRepo() { return depotRepo; }
}
