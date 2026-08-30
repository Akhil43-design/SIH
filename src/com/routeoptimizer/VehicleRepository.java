package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {

    private final DatabaseManager db;

    public VehicleRepository(DatabaseManager db) {
        this.db = db;
    }

    public VehicleEntity save(VehicleEntity entity) {
        if (entity == null || entity.getId() == null) {
            throw new ValidationException("Cannot save null vehicle or vehicle with null ID.");
        }
        if (entity.getCapacity() <= 0) {
            throw new ValidationException("Vehicle capacity must be positive (got: " + entity.getCapacity() + ").");
        }
        if (entity.getFuelConsumptionRate() < 0) {
            throw new ValidationException("Fuel consumption rate cannot be negative.");
        }
        if (entity.getCostPerDistance() < 0) {
            throw new ValidationException("Cost per distance cannot be negative.");
        }
        if (entity.getDepotId() != null && !entity.getDepotId().isEmpty()) {
            if (!db.depots.containsKey(entity.getDepotId())) {
                throw new ValidationException("Referenced depot ID '" + entity.getDepotId() + "' does not exist.");
            }
        }
        entity.setUpdatedAt(System.currentTimeMillis());
        db.vehicles.put(entity.getId(), entity);
        db.flush();
        return entity;
    }

    public VehicleEntity findById(String id) {
        if (id == null) return null;
        VehicleEntity v = db.vehicles.get(id);
        if (v != null && v.isActive()) {
            return v;
        }
        return null;
    }

    public List<VehicleEntity> findAll() {
        List<VehicleEntity> list = new ArrayList<>();
        for (VehicleEntity v : db.vehicles.values()) {
            if (v.isActive()) {
                list.add(v);
            }
        }
        return list;
    }

    public boolean existsById(String id) {
        VehicleEntity v = db.vehicles.get(id);
        return v != null && v.isActive();
    }

    public boolean deleteById(String id) {
        if (id == null) return false;
        VehicleEntity removed = db.vehicles.remove(id);
        if (removed != null) {
            db.flush();
            return true;
        }
        return false;
    }
}
