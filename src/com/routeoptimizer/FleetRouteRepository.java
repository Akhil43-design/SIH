package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class FleetRouteRepository {

    private final DatabaseManager db;

    public FleetRouteRepository(DatabaseManager db) {
        this.db = db;
    }

    public FleetRouteEntity save(FleetRouteEntity entity) {
        if (entity == null || entity.getId() == null) {
            throw new ValidationException("Cannot save null fleet route or route with null ID.");
        }
        if (!db.optimizationRuns.containsKey(entity.getOptimizationId())) {
            throw new ValidationException("Foreign key violation: optimization run '" + entity.getOptimizationId() + "' does not exist.");
        }
        db.fleetRoutes.put(entity.getId(), entity);
        db.flush();
        return entity;
    }

    public List<FleetRouteEntity> findByOptimizationId(String optimizationId) {
        List<FleetRouteEntity> list = new ArrayList<>();
        if (optimizationId == null) return list;
        for (FleetRouteEntity fr : db.fleetRoutes.values()) {
            if (optimizationId.equals(fr.getOptimizationId())) {
                list.add(fr);
            }
        }
        return list;
    }
}
