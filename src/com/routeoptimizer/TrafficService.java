package com.routeoptimizer;

public class TrafficService {

    private final TrafficConfiguration config;
    private final TrafficCache cache;
    private final TrafficDataProvider provider;

    public TrafficService(TrafficConfiguration config, TrafficCache cache) {
        this.config = config != null ? config : new TrafficConfiguration();
        this.cache = cache != null ? cache : new TrafficCache(this.config.getCacheTtlMillis());

        if (this.config.getMode() == TrafficSourceMode.LIVE) {
            this.provider = new ExternalLiveTrafficProvider(this.config, this.cache);
        } else {
            this.provider = new SimulatedTrafficProvider();
        }
    }

    public TrafficService() {
        this(new TrafficConfiguration(), new TrafficCache());
    }

    public TrafficUpdate processUpdate(TrafficUpdateRequest req, Location origin, Location destination) {
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

        return new TrafficUpdate(origin, destination, oldMult, newMult, ts, src);
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
}
