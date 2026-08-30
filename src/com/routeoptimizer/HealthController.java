package com.routeoptimizer;

public class HealthController {

    private final ServerConfiguration config;

    public HealthController(ServerConfiguration config) {
        this.config = config != null ? config : new ServerConfiguration();
    }

    public HealthResponse getHealth() {
        long uptime = System.currentTimeMillis() - config.getServerStartTime();
        return new HealthResponse(
                "UP",
                "QuantumRouteOptimizer",
                "4.0.0",
                config.getRoutingMode(),
                config.getTrafficMode(),
                uptime
        );
    }
}
