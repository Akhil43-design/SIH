package com.routeoptimizer;

public class TrafficMetrics {

    private final double multiplier;
    private final double speedKmh;
    private final double delayMinutes;
    private final long timestamp;
    private final String source;
    private final boolean live;

    public TrafficMetrics(
            double multiplier,
            double speedKmh,
            double delayMinutes,
            long timestamp,
            String source,
            boolean live) {

        this.multiplier = Math.max(0.50, multiplier);
        this.speedKmh = Math.max(1.0, speedKmh);
        this.delayMinutes = Math.max(0.0, delayMinutes);
        this.timestamp = timestamp;
        this.source = source != null ? source : "UNKNOWN";
        this.live = live;
    }

    public static TrafficMetrics createSimulated(double multiplier, double baseTravelTimeMin, long timestamp) {
        double mult = Math.max(0.50, multiplier);
        double delay = Math.max(0.0, baseTravelTimeMin * (mult - 1.0));
        double speed = 40.0 / mult;
        return new TrafficMetrics(mult, speed, delay, timestamp, "SIMULATED", false);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public double getSpeedKmh() {
        return speedKmh;
    }

    public double getDelayMinutes() {
        return delayMinutes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }

    public boolean isLive() {
        return live;
    }

    @Override
    public String toString() {
        return String.format("TrafficMetrics [%.2fx mult, %.1f km/h, +%.1f min delay, src: %s, live: %b]",
                multiplier, speedKmh, delayMinutes, source, live);
    }
}
