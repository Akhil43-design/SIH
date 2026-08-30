package com.routeoptimizer;

public class TrafficEventEntity {

    private String id;
    private String originId;
    private String destinationId;
    private double oldMultiplier;
    private double newMultiplier;
    private long timestamp;
    private String source;
    private String affectedOptimizationId;
    private boolean processed = false;

    public TrafficEventEntity() {}

    public TrafficEventEntity(String id, String originId, String destinationId, double oldMultiplier,
                              double newMultiplier, long timestamp, String source, String affectedOptimizationId) {
        this.id = id;
        this.originId = originId;
        this.destinationId = destinationId;
        this.oldMultiplier = oldMultiplier;
        this.newMultiplier = newMultiplier;
        this.timestamp = timestamp;
        this.source = source;
        this.affectedOptimizationId = affectedOptimizationId;
    }

    public static TrafficEventEntity fromDomain(TrafficUpdate tu, String affectedOptimizationId) {
        if (tu == null) return null;
        String eventId = "tevt-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        return new TrafficEventEntity(
                eventId,
                tu.getOrigin().getId(),
                tu.getDestination().getId(),
                tu.getOldMultiplier(),
                tu.getNewMultiplier(),
                tu.getTimestampMillis(),
                tu.getSource(),
                affectedOptimizationId
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOriginId() { return originId; }
    public void setOriginId(String originId) { this.originId = originId; }
    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }
    public double getOldMultiplier() { return oldMultiplier; }
    public void setOldMultiplier(double oldMultiplier) { this.oldMultiplier = oldMultiplier; }
    public double getNewMultiplier() { return newMultiplier; }
    public void setNewMultiplier(double newMultiplier) { this.newMultiplier = newMultiplier; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getAffectedOptimizationId() { return affectedOptimizationId; }
    public void setAffectedOptimizationId(String affectedOptimizationId) { this.affectedOptimizationId = affectedOptimizationId; }
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
}
