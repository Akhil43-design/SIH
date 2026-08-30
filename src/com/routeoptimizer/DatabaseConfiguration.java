package com.routeoptimizer;

public class DatabaseConfiguration {

    public enum DatabaseType {
        EMBEDDED_PERSISTENT,
        EMBEDDED_IN_MEMORY,
        POSTGRESQL,
        H2
    }

    private DatabaseType type;
    private String url;
    private String username;
    private String password;
    private String storageFilePath;
    private boolean autoMigrate;

    public DatabaseConfiguration() {
        // Read environment variables or use safe embedded defaults
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USERNAME");
        if (dbUser == null) dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");
        String dbTypeEnv = System.getenv("DB_TYPE");

        if (dbUrl != null && !dbUrl.trim().isEmpty()) {
            this.url = dbUrl;
            this.username = dbUser != null ? dbUser : "postgres";
            this.password = dbPass != null ? dbPass : "";
            if (dbUrl.contains("postgresql")) {
                this.type = DatabaseType.POSTGRESQL;
            } else if (dbUrl.contains("h2")) {
                this.type = DatabaseType.H2;
            } else {
                this.type = DatabaseType.EMBEDDED_PERSISTENT;
            }
        } else if ("IN_MEMORY".equalsIgnoreCase(dbTypeEnv)) {
            this.type = DatabaseType.EMBEDDED_IN_MEMORY;
            this.url = "jdbc:memory:quantum_route_optimizer";
            this.username = "sa";
            this.password = "";
        } else {
            this.type = DatabaseType.EMBEDDED_PERSISTENT;
            this.storageFilePath = "quantum_route_optimizer_db.dat";
            this.url = "jdbc:embedded:" + this.storageFilePath;
            this.username = "admin";
            this.password = "";
        }
        this.autoMigrate = true;
    }

    public DatabaseConfiguration(DatabaseType type, String storageFilePath) {
        this.type = type;
        this.storageFilePath = storageFilePath;
        this.url = "jdbc:embedded:" + (storageFilePath != null ? storageFilePath : "memory");
        this.username = "admin";
        this.password = "";
        this.autoMigrate = true;
    }

    public DatabaseType getType() { return type; }
    public void setType(DatabaseType type) { this.type = type; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStorageFilePath() { return storageFilePath; }
    public void setStorageFilePath(String storageFilePath) { this.storageFilePath = storageFilePath; }
    public boolean isAutoMigrate() { return autoMigrate; }
    public void setAutoMigrate(boolean autoMigrate) { this.autoMigrate = autoMigrate; }

    @Override
    public String toString() {
        return "DatabaseConfiguration{" +
                "type=" + type +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", storageFilePath='" + storageFilePath + '\'' +
                ", autoMigrate=" + autoMigrate +
                '}';
    }
}
