package com.routeoptimizer;

public class ServerConfiguration {

    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_ALLOWED_ORIGIN = "*";

    private int port;
    private String allowedOrigins;
    private String routingMode;
    private String trafficMode;
    private long serverStartTime;

    public ServerConfiguration(int port, String allowedOrigins, String routingMode, String trafficMode) {
        this.port = port > 0 ? port : DEFAULT_PORT;
        this.allowedOrigins = allowedOrigins != null ? allowedOrigins : DEFAULT_ALLOWED_ORIGIN;
        this.routingMode = routingMode != null ? routingMode : "SYNTHETIC";
        this.trafficMode = trafficMode != null ? trafficMode : "SIMULATED";
        this.serverStartTime = System.currentTimeMillis();
    }

    public ServerConfiguration() {
        this(DEFAULT_PORT, DEFAULT_ALLOWED_ORIGIN, "SYNTHETIC", "SIMULATED");
    }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getAllowedOrigins() { return allowedOrigins; }
    public void setAllowedOrigins(String allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    public String getRoutingMode() { return routingMode; }
    public void setRoutingMode(String routingMode) { this.routingMode = routingMode; }
    public String getTrafficMode() { return trafficMode; }
    public void setTrafficMode(String trafficMode) { this.trafficMode = trafficMode; }
    public long getServerStartTime() { return serverStartTime; }
}
