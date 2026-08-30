package com.routeoptimizer;

public class TrafficUpdate {

    private final Location origin;
    private final Location destination;
    private final double oldMultiplier;
    private final double newMultiplier;
    private final long timestampMillis;
    private final String source;

    public TrafficUpdate(
            Location origin,
            Location destination,
            double oldMultiplier,
            double newMultiplier,
            long timestampMillis,
            String source) {

        this.origin = origin;
        this.destination = destination;
        this.oldMultiplier = oldMultiplier;
        this.newMultiplier = newMultiplier;
        this.timestampMillis = timestampMillis;
        this.source = source != null ? source : "EVENT";
    }

    public Location getOrigin() {
        return origin;
    }

    public Location getDestination() {
        return destination;
    }

    public double getOldMultiplier() {
        return oldMultiplier;
    }

    public double getNewMultiplier() {
        return newMultiplier;
    }

    public double getRelativeChange() {
        if (oldMultiplier <= 0) return 0.0;
        return Math.abs(newMultiplier - oldMultiplier) / oldMultiplier;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return String.format("TrafficUpdate [%s -> %s: %.2fx to %.2fx (Change: %.1f%%), src: %s]",
                origin.getId(), destination.getId(), oldMultiplier, newMultiplier, getRelativeChange() * 100.0, source);
    }
}
