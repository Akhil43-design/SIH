package com.routeoptimizer;

import java.util.List;

public class TrafficService {

    private final TrafficConfiguration config;
    private final TrafficCache cache;
    private final TrafficDataProvider provider;
    private final TrafficEventRepository eventRepo;

    public TrafficService(TrafficConfiguration config, TrafficCache cache, DatabaseManager db) {
        this.config = config != null ? config : new TrafficConfiguration();
        this.cache = cache != null ? cache : new TrafficCache(this.config.getCacheTtlMillis());
        this.eventRepo = new TrafficEventRepository(db != null ? db : new DatabaseManager());

        if (this.config.getMode() == TrafficSourceMode.LIVE) {
            this.provider = new ExternalLiveTrafficProvider(this.config, this.cache);
        } else {
            this.provider = new SimulatedTrafficProvider();
        }
    }

    public TrafficService(DatabaseManager db) {
        this(new TrafficConfiguration(), new TrafficCache(), db);
    }

    public TrafficService() {
        this(new TrafficConfiguration(), new TrafficCache(), new DatabaseManager());
    }

    public TrafficUpdate processUpdate(TrafficUpdateRequest req, Location origin, Location destination) {
        return processUpdate(req, origin, destination, null);
    }

    public TrafficUpdate processUpdate(TrafficUpdateRequest req, Location origin, Location destination, String affectedOptimizationId) {
        if (req == null) {
            throw new ValidationException("Traffic update request must not be null.");
        }
        req.validate();
        if (origin == null || destination == null) {
            throw new ValidationException("Origin and Destination locations must exist.");
        }

        double oldMult = req.getOldMultiplier() != null ? req.getOldMultiplier() : 1.0;
        double newMult = req.getNewMultiplier();
        long ts = req.getTimestamp() != null ? req.getTimestamp() : System.currentTimeMillis();
        String src = req.getSource() != null ? req.getSource() : "REST_API_UPDATE";

        // Update in cache
        TrafficMetrics metrics = new TrafficMetrics(newMult, 40.0 / newMult, 0.0, ts, src, provider.isAvailable());
        cache.put(origin, destination, ts, metrics);

        TrafficUpdate update = new TrafficUpdate(origin, destination, oldMult, newMult, ts, src);

        // Persist event to database
        TrafficEventEntity eventEntity = TrafficEventEntity.fromDomain(update, affectedOptimizationId);
        eventEntity.setProcessed(true);
        eventRepo.save(eventEntity);

        return update;
    }

    public List<TrafficEventEntity> getAllTrafficEvents() {
        return eventRepo.findAll();
    }

    public List<TrafficEventEntity> getTrafficEventsForOptimization(String optId) {
        return eventRepo.findByAffectedOptimizationId(optId);
    }

    public TrafficMetrics getTrafficMetrics(Location origin, Location destination, long timestamp) {
        return provider.getTraffic(origin, destination, timestamp);
    }

    public TrafficDataProvider getProvider() {
        return provider;
    }

    public TrafficCache getCache() {
        return cache;
    }

    public TrafficConfiguration getConfig() {
        return config;
    }

    public TrafficEventRepository getEventRepo() {
        return eventRepo;
    }
}
