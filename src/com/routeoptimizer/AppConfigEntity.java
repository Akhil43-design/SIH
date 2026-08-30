package com.routeoptimizer;

public class AppConfigEntity {

    private String key;
    private String value;
    private String description;
    private long updatedAt;

    public AppConfigEntity() {
        this.updatedAt = System.currentTimeMillis();
    }

    public AppConfigEntity(String key, String value, String description) {
        this();
        this.key = key;
        this.value = value;
        this.description = description;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
