package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OptimizationRunRepository {

    private final DatabaseManager db;

    public OptimizationRunRepository(DatabaseManager db) {
        this.db = db;
    }

    public OptimizationRunEntity save(OptimizationRunEntity entity) {
        if (entity == null || entity.getId() == null) {
            throw new ValidationException("Cannot save null optimization run or run with null ID.");
        }
        db.optimizationRuns.put(entity.getId(), entity);
        db.flush();
        return entity;
    }

    public OptimizationRunEntity findById(String id) {
        if (id == null) return null;
        return db.optimizationRuns.get(id);
    }

    public List<OptimizationRunEntity> findAll(String statusFilter, Integer limit) {
        List<OptimizationRunEntity> list = new ArrayList<>();
        for (OptimizationRunEntity r : db.optimizationRuns.values()) {
            if (statusFilter == null || statusFilter.trim().isEmpty() || statusFilter.equalsIgnoreCase(r.getStatus())) {
                list.add(r);
            }
        }
        // Sort by start_time descending
        list.sort((a, b) -> Long.compare(b.getStartTime(), a.getStartTime()));
        if (limit != null && limit > 0 && list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }

    public boolean deleteById(String id) {
        if (id == null) return false;
        db.optimizationResults.remove(id);
        List<String> routesToDelete = new ArrayList<>();
        for (FleetRouteEntity fr : db.fleetRoutes.values()) {
            if (id.equals(fr.getOptimizationId())) {
                routesToDelete.add(fr.getId());
            }
        }
        for (String rId : routesToDelete) {
            db.fleetRoutes.remove(rId);
            List<String> stopsToDelete = new ArrayList<>();
            for (RouteStopEntity rs : db.routeStops.values()) {
                if (rId.equals(rs.getFleetRouteId())) {
                    stopsToDelete.add(rs.getId());
                }
            }
            for (String sId : stopsToDelete) {
                db.routeStops.remove(sId);
            }
        }
        OptimizationRunEntity removed = db.optimizationRuns.remove(id);
        db.flush();
        return removed != null;
    }
}
