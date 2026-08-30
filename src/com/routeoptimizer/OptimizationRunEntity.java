package com.routeoptimizer;

public class OptimizationRunEntity {

    private String id;
    private String parentRunId;
    private String status; // QUEUED, RUNNING, COMPLETED, FAILED
    private long startTime;
    private Long completionTime;
    private Long runtimeMs;
    private long seed;
    private int populationSize;
    private int generations;
    private double learningRate;
    private double explorationRate;
    private String routingMode;
    private String trafficMode;
    private String trafficProvider;
    private int requestedCustomerCount;
    private int vehicleCount;
    private int depotCount;
    private String triggerEvent;
    private String errorMessage;
    private long createdAt;

    public OptimizationRunEntity() {
        this.createdAt = System.currentTimeMillis();
        this.startTime = System.currentTimeMillis();
        this.status = "QUEUED";
    }

    public OptimizationRunEntity(String id, long seed, int populationSize, int generations,
                                 double learningRate, double explorationRate, String routingMode,
                                 String trafficMode, String trafficProvider) {
        this();
        this.id = id;
        this.seed = seed;
        this.populationSize = populationSize;
        this.generations = generations;
        this.learningRate = learningRate;
        this.explorationRate = explorationRate;
        this.routingMode = routingMode;
        this.trafficMode = trafficMode;
        this.trafficProvider = trafficProvider;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getParentRunId() { return parentRunId; }
    public void setParentRunId(String parentRunId) { this.parentRunId = parentRunId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public Long getCompletionTime() { return completionTime; }
    public void setCompletionTime(Long completionTime) { this.completionTime = completionTime; }
    public Long getRuntimeMs() { return runtimeMs; }
    public void setRuntimeMs(Long runtimeMs) { this.runtimeMs = runtimeMs; }
    public long getSeed() { return seed; }
    public void setSeed(long seed) { this.seed = seed; }
    public int getPopulationSize() { return populationSize; }
    public void setPopulationSize(int populationSize) { this.populationSize = populationSize; }
    public int getGenerations() { return generations; }
    public void setGenerations(int generations) { this.generations = generations; }
    public double getLearningRate() { return learningRate; }
    public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
    public double getExplorationRate() { return explorationRate; }
    public void setExplorationRate(double explorationRate) { this.explorationRate = explorationRate; }
    public String getRoutingMode() { return routingMode; }
    public void setRoutingMode(String routingMode) { this.routingMode = routingMode; }
    public String getTrafficMode() { return trafficMode; }
    public void setTrafficMode(String trafficMode) { this.trafficMode = trafficMode; }
    public String getTrafficProvider() { return trafficProvider; }
    public void setTrafficProvider(String trafficProvider) { this.trafficProvider = trafficProvider; }
    public int getRequestedCustomerCount() { return requestedCustomerCount; }
    public void setRequestedCustomerCount(int requestedCustomerCount) { this.requestedCustomerCount = requestedCustomerCount; }
    public int getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(int vehicleCount) { this.vehicleCount = vehicleCount; }
    public int getDepotCount() { return depotCount; }
    public void setDepotCount(int depotCount) { this.depotCount = depotCount; }
    public String getTriggerEvent() { return triggerEvent; }
    public void setTriggerEvent(String triggerEvent) { this.triggerEvent = triggerEvent; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
