package com.routeoptimizer;

public class TrafficUpdateRequest {

    private String originId;
    private String destinationId;
    private Double oldMultiplier;
    private Double newMultiplier;
    private Long timestamp;
    private String source;

    public TrafficUpdateRequest() {
    }

    public TrafficUpdateRequest(String originId, String destinationId, Double oldMultiplier,
                                Double newMultiplier, Long timestamp, String source) {
        this.originId = originId;
        this.destinationId = destinationId;
        this.oldMultiplier = oldMultiplier;
        this.newMultiplier = newMultiplier;
        this.timestamp = timestamp;
        this.source = source;
    }

    public void validate() {
        if (originId == null || originId.trim().isEmpty()) {
            throw new ValidationException("Origin location ID is required.");
        }
        if (destinationId == null || destinationId.trim().isEmpty()) {
            throw new ValidationException("Destination location ID is required.");
        }
        if (newMultiplier == null || newMultiplier <= 0) {
            throw new ValidationException("New traffic multiplier must be positive.");
        }
    }

    public String getOriginId() { return originId; }
    public void setOriginId(String originId) { this.originId = originId; }
    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }
    public Double getOldMultiplier() { return oldMultiplier; }
    public void setOldMultiplier(Double oldMultiplier) { this.oldMultiplier = oldMultiplier; }
    public Double getNewMultiplier() { return newMultiplier; }
    public void setNewMultiplier(Double newMultiplier) { this.newMultiplier = newMultiplier; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
