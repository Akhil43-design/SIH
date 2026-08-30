package com.routeoptimizer;

public class OptimizationController {

    private final OptimizationService optimizationService;

    public OptimizationController(OptimizationService optimizationService) {
        this.optimizationService = optimizationService != null ? optimizationService : new OptimizationService();
    }

    public OptimizationResponse runOptimization(OptimizationRequest req) {
        return optimizationService.runOptimization(req);
    }

    public OptimizationResponse getOptimization(String id) {
        return optimizationService.getOptimization(id);
    }

    public OptimizationResponse reoptimize(String id, TrafficUpdateRequest updateReq) {
        return optimizationService.reoptimize(id, updateReq);
    }
}
