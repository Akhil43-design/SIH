package com.routeoptimizer;

public class OptimizationResultRepository {

    private final DatabaseManager db;

    public OptimizationResultRepository(DatabaseManager db) {
        this.db = db;
    }

    public OptimizationResultEntity save(OptimizationResultEntity entity) {
        if (entity == null || entity.getOptimizationId() == null) {
            throw new ValidationException("Cannot save null optimization result or result with null ID.");
        }
        if (!db.optimizationRuns.containsKey(entity.getOptimizationId())) {
            throw new ValidationException("Foreign key violation: optimization run '" + entity.getOptimizationId() + "' does not exist.");
        }
        db.optimizationResults.put(entity.getOptimizationId(), entity);
        db.flush();
        return entity;
    }

    public OptimizationResultEntity findById(String optimizationId) {
        if (optimizationId == null) return null;
        return db.optimizationResults.get(optimizationId);
    }
}
