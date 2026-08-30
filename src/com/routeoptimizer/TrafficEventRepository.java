package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrafficEventRepository {

    private final DatabaseManager db;

    public TrafficEventRepository(DatabaseManager db) {
        this.db = db;
    }

    public TrafficEventEntity save(TrafficEventEntity entity) {
        if (entity == null || entity.getId() == null) {
            throw new ValidationException("Cannot save null traffic event or event with null ID.");
        }
        db.trafficEvents.put(entity.getId(), entity);
        db.flush();
        return entity;
    }

    public TrafficEventEntity findById(String id) {
        if (id == null) return null;
        return db.trafficEvents.get(id);
    }

    public List<TrafficEventEntity> findAll() {
        List<TrafficEventEntity> list = new ArrayList<>(db.trafficEvents.values());
        list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return list;
    }

    public List<TrafficEventEntity> findByAffectedOptimizationId(String optimizationId) {
        List<TrafficEventEntity> list = new ArrayList<>();
        if (optimizationId == null) return list;
        for (TrafficEventEntity te : db.trafficEvents.values()) {
            if (optimizationId.equals(te.getAffectedOptimizationId())) {
                list.add(te);
            }
        }
        return list;
    }
}
