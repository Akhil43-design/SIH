package com.routeoptimizer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TrafficCache {

    private static class CacheEntry {
        final TrafficMetrics metrics;
        final long insertedAt;

        CacheEntry(TrafficMetrics metrics, long insertedAt) {
            this.metrics = metrics;
            this.insertedAt = insertedAt;
        }

        boolean isExpired(long ttlMillis) {
            return (System.currentTimeMillis() - insertedAt) > ttlMillis;
        }
    }

    private final ConcurrentHashMap<String, CacheEntry> cache;
    private final long ttlMillis;
    private final AtomicLong hitCount;
    private final AtomicLong missCount;

    public TrafficCache(long ttlMillis) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlMillis > 0 ? ttlMillis : 5 * 60 * 1000L;
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);
    }

    public TrafficCache() {
        this(5 * 60 * 1000L);
    }

    public static String generateKey(Location origin, Location destination, long timestampMillis, long ttlBucketMs) {
        if (origin == null || destination == null) {
            return "";
        }
        long bucket = timestampMillis / Math.max(1000L, ttlBucketMs);
        return origin.getId() + "->" + destination.getId() + "@" + bucket;
    }

    public TrafficMetrics get(Location origin, Location destination, long timestampMillis) {
        String key = generateKey(origin, destination, timestampMillis, ttlMillis);
        CacheEntry entry = cache.get(key);

        if (entry != null && !entry.isExpired(ttlMillis)) {
            hitCount.incrementAndGet();
            return entry.metrics;
        }

        if (entry != null && entry.isExpired(ttlMillis)) {
            cache.remove(key);
        }

        missCount.incrementAndGet();
        return null;
    }

    public void put(Location origin, Location destination, long timestampMillis, TrafficMetrics metrics) {
        if (origin != null && destination != null && metrics != null) {
            String key = generateKey(origin, destination, timestampMillis, ttlMillis);
            cache.put(key, new CacheEntry(metrics, System.currentTimeMillis()));
        }
    }

    public boolean contains(Location origin, Location destination, long timestampMillis) {
        String key = generateKey(origin, destination, timestampMillis, ttlMillis);
        CacheEntry entry = cache.get(key);
        return entry != null && !entry.isExpired(ttlMillis);
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

    public long getTtlMillis() {
        return ttlMillis;
    }
}
