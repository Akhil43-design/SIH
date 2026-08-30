package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class DepotRepository {

    private final DatabaseManager db;

    public DepotRepository(DatabaseManager db) {
        this.db = db;
    }

    public DepotEntity save(DepotEntity entity) {
        if (entity == null || entity.getId() == null) {
            throw new ValidationException("Cannot save null depot or depot with null ID.");
        }
        entity.setUpdatedAt(System.currentTimeMillis());
        db.depots.put(entity.getId(), entity);
        db.flush();
        return entity;
    }

    public DepotEntity findById(String id) {
        if (id == null) return null;
        DepotEntity d = db.depots.get(id);
        if (d != null && d.isActive()) {
            return d;
        }
        return null;
    }

    public List<DepotEntity> findAll() {
        List<DepotEntity> list = new ArrayList<>();
        for (DepotEntity d : db.depots.values()) {
            if (d.isActive()) {
                list.add(d);
            }
        }
        return list;
    }

    public boolean existsById(String id) {
        DepotEntity d = db.depots.get(id);
        return d != null && d.isActive();
    }

    public boolean deleteById(String id) {
        if (id == null) return false;
        // Check foreign key constraint in vehicles
        for (VehicleEntity v : db.vehicles.values()) {
            if (v.isActive() && id.equals(v.getDepotId())) {
                throw new ApiException(409, "FOREIGN_KEY_VIOLATION",
                        "Cannot delete depot '" + id + "' because vehicle '" + v.getId() + "' is assigned to it.");
            }
        }
        DepotEntity removed = db.depots.remove(id);
        if (removed != null) {
            db.flush();
            return true;
        }
        return false;
    }
}
