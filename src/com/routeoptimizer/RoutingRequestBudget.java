package com.routeoptimizer;

import java.util.concurrent.atomic.AtomicLong;

public class RoutingRequestBudget {
    private final AtomicLong externalRequests;
    private final AtomicLong cacheHits;
    private final AtomicLong cacheMisses;
    private final long maxAllowedRequests;
    private volatile boolean budgetExceeded;

    public RoutingRequestBudget(long maxAllowedRequests) {
        this.externalRequests = new AtomicLong(0);
        this.cacheHits = new AtomicLong(0);
        this.cacheMisses = new AtomicLong(0);
        this.maxAllowedRequests = maxAllowedRequests;
        this.budgetExceeded = false;
    }

    public boolean requestExternalApi() {
        if (budgetExceeded) {
            return false;
        }
        
        long current = externalRequests.incrementAndGet();
        if (current > maxAllowedRequests) {
            budgetExceeded = true;
            return false;
        }
        return true;
    }

    public void recordCacheHit() {
        cacheHits.incrementAndGet();
    }

    public void recordCacheMiss() {
        cacheMisses.incrementAndGet();
    }

    public long getExternalRequests() {
        return externalRequests.get();
    }

    public long getCacheHits() {
        return cacheHits.get();
    }

    public long getCacheMisses() {
        return cacheMisses.get();
    }

    public boolean isBudgetExceeded() {
        return budgetExceeded;
    }
    
    public double getEstimatedApiCost() {
        // Assume $0.0001 per request for estimation
        return externalRequests.get() * 0.0001;
    }

    public void reset() {
        externalRequests.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
        budgetExceeded = false;
    }
}
