package com.routeoptimizer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RoutingCache {

    private final ConcurrentHashMap<String, RouteMetrics> cache;
    private final AtomicLong hitCount;
    private final AtomicLong missCount;

    public RoutingCache() {
        this.cache = new ConcurrentHashMap<>();
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);
    }

    public static String generateKey(GeoLocation origin, GeoLocation destination) {
        if (origin == null || destination == null) {
            return "";
        }
        return String.format("%.5f,%.5f->%.5f,%.5f",
                origin.getLatitude(), origin.getLongitude(),
                destination.getLatitude(), destination.getLongitude());
    }

    public RouteMetrics get(GeoLocation origin, GeoLocation destination) {
        String key = generateKey(origin, destination);
        RouteMetrics metrics = cache.get(key);
        if (metrics != null) {
            hitCount.incrementAndGet();
        } else {
            missCount.incrementAndGet();
        }
        return metrics;
    }

    public void put(GeoLocation origin, GeoLocation destination, RouteMetrics metrics) {
        if (origin != null && destination != null && metrics != null) {
            cache.put(generateKey(origin, destination), metrics);
        }
    }

    public boolean contains(GeoLocation origin, GeoLocation destination) {
        return cache.containsKey(generateKey(origin, destination));
    }

    public void clear() {
        cache.clear();
        hitCount.set(0);
        missCount.set(0);
    }

    public int size() {
        return cache.size();
    }

    public long getHitCount() {
        return hitCount.get();
    }

    public long getMissCount() {
        return missCount.get();
    }

    public ConcurrentHashMap<String, RouteMetrics> getCacheEntries() {
        return new ConcurrentHashMap<>(cache);
    }
}
