package com.routeoptimizer;

public class TrafficConfiguration {

    public static final String DEFAULT_TRAFFIC_ENDPOINT = "https://api.tomtom.com/traffic/services/4/flowSegmentData";
    public static final long DEFAULT_CACHE_TTL_MS = 5 * 60 * 1000L; // 5 minutes TTL
    public static final int DEFAULT_TIMEOUT_SECONDS = 5;

    private TrafficSourceMode mode;
    private String apiEndpoint;
    private String apiKey;
    private long cacheTtlMillis;
    private int timeoutSeconds;
    private boolean fallbackEnabled;
    private double reoptimizationThreshold; // e.g. 0.15 for 15% traffic surge

    public TrafficConfiguration(
            TrafficSourceMode mode,
            String apiEndpoint,
            String apiKey,
            long cacheTtlMillis,
            int timeoutSeconds,
            boolean fallbackEnabled,
            double reoptimizationThreshold) {

        this.mode = mode != null ? mode : TrafficSourceMode.SIMULATED;
        this.apiEndpoint = (apiEndpoint != null && !apiEndpoint.trim().isEmpty()) ? apiEndpoint.trim() : DEFAULT_TRAFFIC_ENDPOINT;
        this.apiKey = (apiKey != null && !apiKey.trim().isEmpty()) ? apiKey.trim() : System.getenv("TRAFFIC_API_KEY");
        this.cacheTtlMillis = cacheTtlMillis > 0 ? cacheTtlMillis : DEFAULT_CACHE_TTL_MS;
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : DEFAULT_TIMEOUT_SECONDS;
        this.fallbackEnabled = fallbackEnabled;
        this.reoptimizationThreshold = reoptimizationThreshold > 0 ? reoptimizationThreshold : 0.15;
    }

    public TrafficConfiguration() {
        this(TrafficSourceMode.SIMULATED, DEFAULT_TRAFFIC_ENDPOINT, null, DEFAULT_CACHE_TTL_MS, DEFAULT_TIMEOUT_SECONDS, true, 0.15);
    }

    public static TrafficConfiguration createLiveWithFallback(String apiKey) {
        return new TrafficConfiguration(TrafficSourceMode.LIVE, DEFAULT_TRAFFIC_ENDPOINT, apiKey, DEFAULT_CACHE_TTL_MS, DEFAULT_TIMEOUT_SECONDS, true, 0.15);
    }

    public TrafficSourceMode getMode() {
        return mode;
    }

    public void setMode(TrafficSourceMode mode) {
        this.mode = mode;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public long getCacheTtlMillis() {
        return cacheTtlMillis;
    }

    public void setCacheTtlMillis(long cacheTtlMillis) {
        this.cacheTtlMillis = cacheTtlMillis;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }

    public void setFallbackEnabled(boolean fallbackEnabled) {
        this.fallbackEnabled = fallbackEnabled;
    }

    public double getReoptimizationThreshold() {
        return reoptimizationThreshold;
    }

    public void setReoptimizationThreshold(double reoptimizationThreshold) {
        this.reoptimizationThreshold = reoptimizationThreshold;
    }
}
