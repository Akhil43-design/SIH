package com.routeoptimizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoutingMatrixManager {
    
    // Simple edge representation
    private static class Edge {
        String fromId;
        String toId;
        
        Edge(String from, String to) {
            this.fromId = from;
            this.toId = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return fromId.equals(edge.fromId) && toId.equals(edge.toId);
        }

        @Override
        public int hashCode() {
            return 31 * fromId.hashCode() + toId.hashCode();
        }
    }

    private final Map<Edge, Double> distanceCache;
    private final RoutingRequestBudget budget;
    private final RoadNetwork fallbackNetwork;

    public RoutingMatrixManager(RoutingRequestBudget budget, RoadNetwork fallbackNetwork) {
        this.distanceCache = new ConcurrentHashMap<>();
        this.budget = budget;
        this.fallbackNetwork = fallbackNetwork;
    }

    public double getDistance(Location from, Location to) {
        if (from.getId().equals(to.getId())) {
            return 0.0;
        }

        Edge edge = new Edge(from.getId(), to.getId());
        
        Double cached = distanceCache.get(edge);
        if (cached != null) {
            budget.recordCacheHit();
            return cached;
        }
        
        budget.recordCacheMiss();

        double dist;
        if (budget.requestExternalApi()) {
            // Ideally call OSRM here. Since this is an architectural layer without actual OSRM logic inside the manager,
            // we will simulate the distance calculation using the fallback network.
            dist = calculateFallbackDistance(from, to);
        } else {
            dist = calculateFallbackDistance(from, to);
        }
        
        // Memory cap protection: Do not cache indefinitely if we hit massive scales
        if (distanceCache.size() < 1_000_000) {
            distanceCache.put(edge, dist);
        }

        return dist;
    }

    private double calculateFallbackDistance(Location from, Location to) {
        if (fallbackNetwork != null) {
            Road road = fallbackNetwork.findRoad(from, to);
            if (road != null) {
                return road.getDistance();
            }
        }
        // Fallback to Haversine if no road network is provided and they are GeoLocations
        if (from instanceof GeoLocation && to instanceof GeoLocation) {
            return calculateHaversine((GeoLocation) from, (GeoLocation) to);
        }
        // Default worst-case dummy distance
        return 99999.0;
    }

    private double calculateHaversine(GeoLocation loc1, GeoLocation loc2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double dLon = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lat2 = Math.toRadians(loc2.getLatitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    public int getCacheSize() {
        return distanceCache.size();
    }
    
    public void clearCache() {
        distanceCache.clear();
    }
}
