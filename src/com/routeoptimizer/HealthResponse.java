package com.routeoptimizer;

public class HealthResponse {

    private String status;
    private String applicationName;
    private String version;
    private String routingMode;
    private String trafficMode;
    private long uptimeMs;

    public HealthResponse(String status, String applicationName, String version,
                          String routingMode, String trafficMode, long uptimeMs) {
        this.status = status;
        this.applicationName = applicationName;
        this.version = version;
        this.routingMode = routingMode;
        this.trafficMode = trafficMode;
        this.uptimeMs = uptimeMs;
    }

    public HealthResponse() {
        this("UP", "QuantumRouteOptimizer", "4.0.0", "SYNTHETIC", "SIMULATED", 0L);
    }

    public String getStatus() { return status; }
    public String getApplicationName() { return applicationName; }
    public String getVersion() { return version; }
    public String getRoutingMode() { return routingMode; }
    public String getTrafficMode() { return trafficMode; }
    public long getUptimeMs() { return uptimeMs; }
}
