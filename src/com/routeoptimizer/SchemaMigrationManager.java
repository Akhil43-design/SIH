package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemaMigrationManager {

    public static final String SCHEMA_VERSION = "1.0.0";

    public static List<String> getPostgreSqlDdl() {
        List<String> ddl = new ArrayList<>();

        ddl.add("CREATE TABLE IF NOT EXISTS depots (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "latitude DOUBLE PRECISION NOT NULL, " +
                "longitude DOUBLE PRECISION NOT NULL, " +
                "active BOOLEAN DEFAULT TRUE, " +
                "created_at BIGINT NOT NULL, " +
                "updated_at BIGINT NOT NULL" +
                ");");

        ddl.add("CREATE TABLE IF NOT EXISTS vehicles (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "capacity DOUBLE PRECISION NOT NULL CHECK (capacity > 0), " +
                "fuel_consumption_rate DOUBLE PRECISION NOT NULL CHECK (fuel_consumption_rate >= 0), " +
                "cost_per_distance DOUBLE PRECISION NOT NULL CHECK (cost_per_distance >= 0), " +
                "depot_id VARCHAR(64) REFERENCES depots(id), " +
                "active BOOLEAN DEFAULT TRUE, " +
                "created_at BIGINT NOT NULL, " +
                "updated_at BIGINT NOT NULL" +
                ");");

        ddl.add("CREATE TABLE IF NOT EXISTS customers (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "latitude DOUBLE PRECISION NOT NULL, " +
                "longitude DOUBLE PRECISION NOT NULL, " +
                "demand DOUBLE PRECISION NOT NULL CHECK (demand >= 0), " +
                "priority VARCHAR(32) NOT NULL, " +
                "service_time DOUBLE PRECISION NOT NULL CHECK (service_time >= 0), " +
                "earliest_time DOUBLE PRECISION NOT NULL CHECK (earliest_time >= 0), " +
                "latest_time DOUBLE PRECISION NOT NULL CHECK (latest_time >= earliest_time), " +
                "active BOOLEAN DEFAULT TRUE, " +
                "cancelled BOOLEAN DEFAULT FALSE, " +
                "created_at BIGINT NOT NULL, " +
                "updated_at BIGINT NOT NULL" +
                ");");

        ddl.add("CREATE TABLE IF NOT EXISTS optimization_runs (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "parent_run_id VARCHAR(64), " +
                "status VARCHAR(32) NOT NULL, " +
                "start_time BIGINT NOT NULL, " +
                "completion_time BIGINT, " +
                "runtime_ms BIGINT, " +
                "seed BIGINT NOT NULL, " +
                "population_size INT NOT NULL, " +
                "generations INT NOT NULL, " +
                "learning_rate DOUBLE PRECISION NOT NULL, " +
                "exploration_rate DOUBLE PRECISION NOT NULL, " +
                "routing_mode VARCHAR(64), " +
                "traffic_mode VARCHAR(64), " +
                "traffic_provider VARCHAR(128), " +
                "requested_customer_count INT, " +
                "vehicle_count INT, " +
                "depot_count INT, " +
                "trigger_event VARCHAR(255), " +
                "error_message TEXT, " +
                "created_at BIGINT NOT NULL" +
                ");");

        ddl.add("CREATE TABLE IF NOT EXISTS optimization_results (" +
                "optimization_id VARCHAR(64) PRIMARY KEY REFERENCES optimization_runs(id) ON DELETE CASCADE, " +
                "total_distance DOUBLE PRECISION NOT NULL, " +
                "total_travel_time DOUBLE PRECISION NOT NULL, " +
                "total_fuel DOUBLE PRECISION NOT NULL, " +
                "total_cost DOUBLE PRECISION NOT NULL, " +
                "optimization_score DOUBLE PRECISION NOT NULL, " +
                "capacity_violations INT NOT NULL, " +
                "time_violations INT NOT NULL, " +
                "lateness DOUBLE PRECISION NOT NULL, " +
                "waiting_time DOUBLE PRECISION NOT NULL, " +
                "unassigned_customers INT NOT NULL, " +
                "duplicate_customers INT NOT NULL, " +
                "runtime_ms BIGINT NOT NULL, " +
                "created_at BIGINT NOT NULL" +
                ");");

        ddl.add("CREATE TABLE IF NOT EXISTS fleet_routes (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "optimization_id VARCHAR(64) NOT NULL REFERENCES optimization_runs(id) ON DELETE CASCADE, " +
                "vehicle_id VARCHAR(64) NOT NULL, " +
                "depot_id VARCHAR(64) NOT NULL, " +
                "total_distance DOUBLE PRECISION NOT NULL, " +
                "total_travel_time DOUBLE PRECISION NOT NULL, " +
                "total_fuel DOUBLE PRECISION NOT NULL, " +
                "total_cost DOUBLE PRECISION NOT NULL, " +
                "route_score DOUBLE PRECISION NOT NULL, " +
                "total_demand DOUBLE PRECISION NOT NULL, " +
                "capacity_violation DOUBLE PRECISION NOT NULL, " +
                "time_violations INT NOT NULL, " +
                "lateness DOUBLE PRECISION NOT NULL, " +
                "waiting_time DOUBLE PRECISION NOT NULL" +
                ");");

        ddl.add("CREATE TABLE IF NOT EXISTS route_stops (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "fleet_route_id VARCHAR(64) NOT NULL REFERENCES fleet_routes(id) ON DELETE CASCADE, " +
                "customer_id VARCHAR(64) NOT NULL, " +
                "sequence_num INT NOT NULL, " +
                "arrival_time DOUBLE PRECISION NOT NULL, " +
                "service_start_time DOUBLE PRECISION NOT NULL, " +
                "departure_time DOUBLE PRECISION NOT NULL, " +
                "waiting_time DOUBLE PRECISION NOT NULL, " +
                "lateness DOUBLE PRECISION NOT NULL, " +
                "completed BOOLEAN DEFAULT FALSE" +
                ");");

        ddl.add("CREATE TABLE IF NOT EXISTS traffic_events (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "origin_id VARCHAR(64) NOT NULL, " +
                "destination_id VARCHAR(64) NOT NULL, " +
                "old_multiplier DOUBLE PRECISION NOT NULL, " +
                "new_multiplier DOUBLE PRECISION NOT NULL, " +
                "timestamp BIGINT NOT NULL, " +
                "source VARCHAR(128) NOT NULL, " +
                "affected_optimization_id VARCHAR(64), " +
                "processed BOOLEAN DEFAULT FALSE" +
                ");");

        ddl.add("CREATE TABLE IF NOT EXISTS application_configuration (" +
                "config_key VARCHAR(128) PRIMARY KEY, " +
                "config_value TEXT NOT NULL, " +
                "description VARCHAR(255), " +
                "updated_at BIGINT NOT NULL" +
                ");");

        return Collections.unmodifiableList(ddl);
    }
}
