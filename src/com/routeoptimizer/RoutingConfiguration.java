package com.routeoptimizer;

public class RoutingConfiguration {

    public static final String DEFAULT_OSRM_URL = "https://router.project-osrm.org";
    public static final int DEFAULT_TIMEOUT_SECONDS = 5;

    private String osrmBaseUrl;
    private int timeoutSeconds;
    private RoutingMode mode;
    private boolean cacheEnabled;

    public RoutingConfiguration(String osrmBaseUrl, int timeoutSeconds, RoutingMode mode, boolean cacheEnabled) {
        this.osrmBaseUrl = (osrmBaseUrl != null && !osrmBaseUrl.trim().isEmpty()) ? osrmBaseUrl.trim() : DEFAULT_OSRM_URL;
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : DEFAULT_TIMEOUT_SECONDS;
        this.mode = mode != null ? mode : RoutingMode.OSRM;
        this.cacheEnabled = cacheEnabled;
    }

    public RoutingConfiguration() {
        this(DEFAULT_OSRM_URL, DEFAULT_TIMEOUT_SECONDS, RoutingMode.OSRM, true);
    }

    public String getOsrmBaseUrl() {
        return osrmBaseUrl;
    }

    public void setOsrmBaseUrl(String osrmBaseUrl) {
        this.osrmBaseUrl = osrmBaseUrl;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public RoutingMode getMode() {
        return mode;
    }

    public void setMode(RoutingMode mode) {
        this.mode = mode;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
}
