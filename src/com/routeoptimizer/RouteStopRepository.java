package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RouteStopRepository {

    private final DatabaseManager db;

    public RouteStopRepository(DatabaseManager db) {
        this.db = db;
    }

    public RouteStopEntity save(RouteStopEntity entity) {
        if (entity == null || entity.getId() == null) {
            throw new ValidationException("Cannot save null route stop or stop with null ID.");
        }
        if (!db.fleetRoutes.containsKey(entity.getFleetRouteId())) {
            throw new ValidationException("Foreign key violation: fleet route '" + entity.getFleetRouteId() + "' does not exist.");
        }
        db.routeStops.put(entity.getId(), entity);
        db.flush();
        return entity;
    }

    public List<RouteStopEntity> findByFleetRouteId(String fleetRouteId) {
        List<RouteStopEntity> list = new ArrayList<>();
        if (fleetRouteId == null) return list;
        for (RouteStopEntity rs : db.routeStops.values()) {
            if (fleetRouteId.equals(rs.getFleetRouteId())) {
                list.add(rs);
            }
        }
        list.sort(Comparator.comparingInt(RouteStopEntity::getSequenceNum));
        return list;
    }
}
