package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class AppConfigRepository {

    private final DatabaseManager db;

    public AppConfigRepository(DatabaseManager db) {
        this.db = db;
    }

    public AppConfigEntity save(AppConfigEntity entity) {
        if (entity == null || entity.getKey() == null) {
            throw new ValidationException("Cannot save null config or config with null key.");
        }
        entity.setUpdatedAt(System.currentTimeMillis());
        db.appConfigs.put(entity.getKey(), entity);
        db.flush();
        return entity;
    }

    public AppConfigEntity findByKey(String key) {
        if (key == null) return null;
        return db.appConfigs.get(key);
    }

    public List<AppConfigEntity> findAll() {
        return new ArrayList<>(db.appConfigs.values());
    }
}
