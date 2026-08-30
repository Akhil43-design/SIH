package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    private final DatabaseManager db;

    public CustomerRepository(DatabaseManager db) {
        this.db = db;
    }

    public CustomerEntity save(CustomerEntity entity) {
        if (entity == null || entity.getId() == null) {
            throw new ValidationException("Cannot save null customer or customer with null ID.");
        }
        if (entity.getDemand() < 0) {
            throw new ValidationException("Customer demand cannot be negative (got: " + entity.getDemand() + ").");
        }
        if (entity.getServiceTime() < 0) {
            throw new ValidationException("Customer service time cannot be negative.");
        }
        if (entity.getEarliestTime() < 0 || entity.getLatestTime() < entity.getEarliestTime()) {
            throw new ValidationException("Invalid customer time window [" + entity.getEarliestTime() + ", " + entity.getLatestTime() + "].");
        }
        entity.setUpdatedAt(System.currentTimeMillis());
        db.customers.put(entity.getId(), entity);
        db.flush();
        return entity;
    }

    public CustomerEntity findById(String id) {
        if (id == null) return null;
        CustomerEntity c = db.customers.get(id);
        if (c != null && c.isActive()) {
            return c;
        }
        return null;
    }

    public List<CustomerEntity> findAll() {
        List<CustomerEntity> list = new ArrayList<>();
        for (CustomerEntity c : db.customers.values()) {
            if (c.isActive()) {
                list.add(c);
            }
        }
        return list;
    }

    public List<CustomerEntity> findAllActiveForOptimization() {
        List<CustomerEntity> list = new ArrayList<>();
        for (CustomerEntity c : db.customers.values()) {
            if (c.isActive() && !c.isCancelled()) {
                list.add(c);
            }
        }
        return list;
    }

    public boolean existsById(String id) {
        CustomerEntity c = db.customers.get(id);
        return c != null && c.isActive();
    }

    public boolean deleteById(String id) {
        if (id == null) return false;
        CustomerEntity removed = db.customers.remove(id);
        if (removed != null) {
            db.flush();
            return true;
        }
        return false;
    }
}
