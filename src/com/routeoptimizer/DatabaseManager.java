package com.routeoptimizer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseManager {

    private final DatabaseConfiguration config;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // In-memory relational tables
    final Map<String, DepotEntity> depots = new ConcurrentHashMap<>();
    final Map<String, VehicleEntity> vehicles = new ConcurrentHashMap<>();
    final Map<String, CustomerEntity> customers = new ConcurrentHashMap<>();
    final Map<String, OptimizationRunEntity> optimizationRuns = new ConcurrentHashMap<>();
    final Map<String, OptimizationResultEntity> optimizationResults = new ConcurrentHashMap<>();
    final Map<String, FleetRouteEntity> fleetRoutes = new ConcurrentHashMap<>();
    final Map<String, RouteStopEntity> routeStops = new ConcurrentHashMap<>();
    final Map<String, TrafficEventEntity> trafficEvents = new ConcurrentHashMap<>();
    final Map<String, AppConfigEntity> appConfigs = new ConcurrentHashMap<>();

    private boolean inTransaction = false;

    public DatabaseManager(DatabaseConfiguration config) {
        this.config = config != null ? config : new DatabaseConfiguration();
        if (this.config.getType() == DatabaseConfiguration.DatabaseType.EMBEDDED_PERSISTENT) {
            loadFromDisk();
        }
    }

    public DatabaseManager() {
        this(new DatabaseConfiguration());
    }

    public void beginTransaction() {
        lock.writeLock().lock();
        inTransaction = true;
    }

    public void commit() {
        try {
            if (config.getType() == DatabaseConfiguration.DatabaseType.EMBEDDED_PERSISTENT) {
                saveToDisk();
            }
            inTransaction = false;
        } finally {
            if (lock.writeLock().isHeldByCurrentThread()) {
                lock.writeLock().unlock();
            }
        }
    }

    public void rollback() {
        try {
            if (config.getType() == DatabaseConfiguration.DatabaseType.EMBEDDED_PERSISTENT) {
                loadFromDisk();
            }
            inTransaction = false;
        } finally {
            if (lock.writeLock().isHeldByCurrentThread()) {
                lock.writeLock().unlock();
            }
        }
    }

    public void clearAll() {
        lock.writeLock().lock();
        try {
            depots.clear();
            vehicles.clear();
            customers.clear();
            optimizationRuns.clear();
            optimizationResults.clear();
            fleetRoutes.clear();
            routeStops.clear();
            trafficEvents.clear();
            appConfigs.clear();
            if (config.getType() == DatabaseConfiguration.DatabaseType.EMBEDDED_PERSISTENT) {
                saveToDisk();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void flush() {
        lock.writeLock().lock();
        try {
            if (config.getType() == DatabaseConfiguration.DatabaseType.EMBEDDED_PERSISTENT) {
                saveToDisk();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void reload() {
        lock.writeLock().lock();
        try {
            depots.clear();
            vehicles.clear();
            customers.clear();
            optimizationRuns.clear();
            optimizationResults.clear();
            fleetRoutes.clear();
            routeStops.clear();
            trafficEvents.clear();
            appConfigs.clear();
            if (config.getType() == DatabaseConfiguration.DatabaseType.EMBEDDED_PERSISTENT) {
                loadFromDisk();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private synchronized void saveToDisk() {
        String filePath = config.getStorageFilePath();
        if (filePath == null) return;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeUTF("QRO_DB_V1");

            // Depots
            dos.writeInt(depots.size());
            for (DepotEntity d : depots.values()) {
                dos.writeUTF(d.getId());
                dos.writeUTF(d.getName());
                dos.writeDouble(d.getLatitude());
                dos.writeDouble(d.getLongitude());
                dos.writeBoolean(d.isActive());
                dos.writeLong(d.getCreatedAt());
                dos.writeLong(d.getUpdatedAt());
            }

            // Vehicles
            dos.writeInt(vehicles.size());
            for (VehicleEntity v : vehicles.values()) {
                dos.writeUTF(v.getId());
                dos.writeUTF(v.getName());
                dos.writeDouble(v.getCapacity());
                dos.writeDouble(v.getFuelConsumptionRate());
                dos.writeDouble(v.getCostPerDistance());
                dos.writeUTF(v.getDepotId() != null ? v.getDepotId() : "");
                dos.writeBoolean(v.isActive());
                dos.writeLong(v.getCreatedAt());
                dos.writeLong(v.getUpdatedAt());
            }

            // Customers
            dos.writeInt(customers.size());
            for (CustomerEntity c : customers.values()) {
                dos.writeUTF(c.getId());
                dos.writeUTF(c.getName());
                dos.writeDouble(c.getLatitude());
                dos.writeDouble(c.getLongitude());
                dos.writeDouble(c.getDemand());
                dos.writeUTF(c.getPriority());
                dos.writeDouble(c.getServiceTime());
                dos.writeDouble(c.getEarliestTime());
                dos.writeDouble(c.getLatestTime());
                dos.writeBoolean(c.isActive());
                dos.writeBoolean(c.isCancelled());
                dos.writeLong(c.getCreatedAt());
                dos.writeLong(c.getUpdatedAt());
            }

            // Optimization Runs
            dos.writeInt(optimizationRuns.size());
            for (OptimizationRunEntity r : optimizationRuns.values()) {
                dos.writeUTF(r.getId());
                dos.writeUTF(r.getParentRunId() != null ? r.getParentRunId() : "");
                dos.writeUTF(r.getStatus());
                dos.writeLong(r.getStartTime());
                dos.writeLong(r.getCompletionTime() != null ? r.getCompletionTime() : 0L);
                dos.writeLong(r.getRuntimeMs() != null ? r.getRuntimeMs() : 0L);
                dos.writeLong(r.getSeed());
                dos.writeInt(r.getPopulationSize());
                dos.writeInt(r.getGenerations());
                dos.writeDouble(r.getLearningRate());
                dos.writeDouble(r.getExplorationRate());
                dos.writeUTF(r.getRoutingMode() != null ? r.getRoutingMode() : "");
                dos.writeUTF(r.getTrafficMode() != null ? r.getTrafficMode() : "");
                dos.writeUTF(r.getTrafficProvider() != null ? r.getTrafficProvider() : "");
                dos.writeInt(r.getRequestedCustomerCount());
                dos.writeInt(r.getVehicleCount());
                dos.writeInt(r.getDepotCount());
                dos.writeUTF(r.getTriggerEvent() != null ? r.getTriggerEvent() : "");
                dos.writeUTF(r.getErrorMessage() != null ? r.getErrorMessage() : "");
                dos.writeLong(r.getCreatedAt());
            }

            // Optimization Results
            dos.writeInt(optimizationResults.size());
            for (OptimizationResultEntity res : optimizationResults.values()) {
                dos.writeUTF(res.getOptimizationId());
                dos.writeDouble(res.getTotalDistance());
                dos.writeDouble(res.getTotalTravelTime());
                dos.writeDouble(res.getTotalFuel());
                dos.writeDouble(res.getTotalCost());
                dos.writeDouble(res.getOptimizationScore());
                dos.writeInt(res.getCapacityViolations());
                dos.writeInt(res.getTimeViolations());
                dos.writeDouble(res.getLateness());
                dos.writeDouble(res.getWaitingTime());
                dos.writeInt(res.getUnassignedCustomers());
                dos.writeInt(res.getDuplicateCustomers());
                dos.writeLong(res.getRuntimeMs());
                dos.writeLong(res.getCreatedAt());
            }

            // Fleet Routes
            dos.writeInt(fleetRoutes.size());
            for (FleetRouteEntity fr : fleetRoutes.values()) {
                dos.writeUTF(fr.getId());
                dos.writeUTF(fr.getOptimizationId());
                dos.writeUTF(fr.getVehicleId());
                dos.writeUTF(fr.getDepotId());
                dos.writeDouble(fr.getTotalDistance());
                dos.writeDouble(fr.getTotalTravelTime());
                dos.writeDouble(fr.getTotalFuel());
                dos.writeDouble(fr.getTotalCost());
                dos.writeDouble(fr.getRouteScore());
                dos.writeDouble(fr.getTotalDemand());
                dos.writeDouble(fr.getCapacityViolation());
                dos.writeInt(fr.getTimeViolations());
                dos.writeDouble(fr.getLateness());
                dos.writeDouble(fr.getWaitingTime());
            }

            // Route Stops
            dos.writeInt(routeStops.size());
            for (RouteStopEntity rs : routeStops.values()) {
                dos.writeUTF(rs.getId());
                dos.writeUTF(rs.getFleetRouteId());
                dos.writeUTF(rs.getCustomerId());
                dos.writeInt(rs.getSequenceNum());
                dos.writeDouble(rs.getArrivalTime());
                dos.writeDouble(rs.getServiceStartTime());
                dos.writeDouble(rs.getDepartureTime());
                dos.writeDouble(rs.getWaitingTime());
                dos.writeDouble(rs.getLateness());
                dos.writeBoolean(rs.isCompleted());
            }

            // Traffic Events
            dos.writeInt(trafficEvents.size());
            for (TrafficEventEntity te : trafficEvents.values()) {
                dos.writeUTF(te.getId());
                dos.writeUTF(te.getOriginId());
                dos.writeUTF(te.getDestinationId());
                dos.writeDouble(te.getOldMultiplier());
                dos.writeDouble(te.getNewMultiplier());
                dos.writeLong(te.getTimestamp());
                dos.writeUTF(te.getSource());
                dos.writeUTF(te.getAffectedOptimizationId() != null ? te.getAffectedOptimizationId() : "");
                dos.writeBoolean(te.isProcessed());
            }

            dos.flush();
            Files.write(Paths.get(filePath), baos.toByteArray());

        } catch (Exception e) {
            System.err.println("Failed to persist database to disk: " + e.getMessage());
        }
    }

    private synchronized void loadFromDisk() {
        String filePath = config.getStorageFilePath();
        if (filePath == null) return;
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return;

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            String header = dis.readUTF();
            if (!"QRO_DB_V1".equals(header)) return;

            // Depots
            int depotCount = dis.readInt();
            for (int i = 0; i < depotCount; i++) {
                DepotEntity d = new DepotEntity();
                d.setId(dis.readUTF());
                d.setName(dis.readUTF());
                d.setLatitude(dis.readDouble());
                d.setLongitude(dis.readDouble());
                d.setActive(dis.readBoolean());
                d.setCreatedAt(dis.readLong());
                d.setUpdatedAt(dis.readLong());
                depots.put(d.getId(), d);
            }

            // Vehicles
            int vehCount = dis.readInt();
            for (int i = 0; i < vehCount; i++) {
                VehicleEntity v = new VehicleEntity();
                v.setId(dis.readUTF());
                v.setName(dis.readUTF());
                v.setCapacity(dis.readDouble());
                v.setFuelConsumptionRate(dis.readDouble());
                v.setCostPerDistance(dis.readDouble());
                String depId = dis.readUTF();
                v.setDepotId(depId.isEmpty() ? null : depId);
                v.setActive(dis.readBoolean());
                v.setCreatedAt(dis.readLong());
                v.setUpdatedAt(dis.readLong());
                vehicles.put(v.getId(), v);
            }

            // Customers
            int custCount = dis.readInt();
            for (int i = 0; i < custCount; i++) {
                CustomerEntity c = new CustomerEntity();
                c.setId(dis.readUTF());
                c.setName(dis.readUTF());
                c.setLatitude(dis.readDouble());
                c.setLongitude(dis.readDouble());
                c.setDemand(dis.readDouble());
                c.setPriority(dis.readUTF());
                c.setServiceTime(dis.readDouble());
                c.setEarliestTime(dis.readDouble());
                c.setLatestTime(dis.readDouble());
                c.setActive(dis.readBoolean());
                c.setCancelled(dis.readBoolean());
                c.setCreatedAt(dis.readLong());
                c.setUpdatedAt(dis.readLong());
                customers.put(c.getId(), c);
            }

            // Optimization Runs
            int runCount = dis.readInt();
            for (int i = 0; i < runCount; i++) {
                OptimizationRunEntity r = new OptimizationRunEntity();
                r.setId(dis.readUTF());
                String pId = dis.readUTF();
                r.setParentRunId(pId.isEmpty() ? null : pId);
                r.setStatus(dis.readUTF());
                r.setStartTime(dis.readLong());
                long cTime = dis.readLong();
                r.setCompletionTime(cTime > 0 ? cTime : null);
                long rTime = dis.readLong();
                r.setRuntimeMs(rTime > 0 ? rTime : null);
                r.setSeed(dis.readLong());
                r.setPopulationSize(dis.readInt());
                r.setGenerations(dis.readInt());
                r.setLearningRate(dis.readDouble());
                r.setExplorationRate(dis.readDouble());
                r.setRoutingMode(dis.readUTF());
                r.setTrafficMode(dis.readUTF());
                r.setTrafficProvider(dis.readUTF());
                r.setRequestedCustomerCount(dis.readInt());
                r.setVehicleCount(dis.readInt());
                r.setDepotCount(dis.readInt());
                String trig = dis.readUTF();
                r.setTriggerEvent(trig.isEmpty() ? null : trig);
                String err = dis.readUTF();
                r.setErrorMessage(err.isEmpty() ? null : err);
                r.setCreatedAt(dis.readLong());
                optimizationRuns.put(r.getId(), r);
            }

            // Optimization Results
            int resCount = dis.readInt();
            for (int i = 0; i < resCount; i++) {
                OptimizationResultEntity res = new OptimizationResultEntity();
                res.setOptimizationId(dis.readUTF());
                res.setTotalDistance(dis.readDouble());
                res.setTotalTravelTime(dis.readDouble());
                res.setTotalFuel(dis.readDouble());
                res.setTotalCost(dis.readDouble());
                res.setOptimizationScore(dis.readDouble());
                res.setCapacityViolations(dis.readInt());
                res.setTimeViolations(dis.readInt());
                res.setLateness(dis.readDouble());
                res.setWaitingTime(dis.readDouble());
                res.setUnassignedCustomers(dis.readInt());
                res.setDuplicateCustomers(dis.readInt());
                res.setRuntimeMs(dis.readLong());
                res.setCreatedAt(dis.readLong());
                optimizationResults.put(res.getOptimizationId(), res);
            }

            // Fleet Routes
            int routeCount = dis.readInt();
            for (int i = 0; i < routeCount; i++) {
                FleetRouteEntity fr = new FleetRouteEntity();
                fr.setId(dis.readUTF());
                fr.setOptimizationId(dis.readUTF());
                fr.setVehicleId(dis.readUTF());
                fr.setDepotId(dis.readUTF());
                fr.setTotalDistance(dis.readDouble());
                fr.setTotalTravelTime(dis.readDouble());
                fr.setTotalFuel(dis.readDouble());
                fr.setTotalCost(dis.readDouble());
                fr.setRouteScore(dis.readDouble());
                fr.setTotalDemand(dis.readDouble());
                fr.setCapacityViolation(dis.readDouble());
                fr.setTimeViolations(dis.readInt());
                fr.setLateness(dis.readDouble());
                fr.setWaitingTime(dis.readDouble());
                fleetRoutes.put(fr.getId(), fr);
            }

            // Route Stops
            int stopCount = dis.readInt();
            for (int i = 0; i < stopCount; i++) {
                RouteStopEntity rs = new RouteStopEntity();
                rs.setId(dis.readUTF());
                rs.setFleetRouteId(dis.readUTF());
                rs.setCustomerId(dis.readUTF());
                rs.setSequenceNum(dis.readInt());
                rs.setArrivalTime(dis.readDouble());
                rs.setServiceStartTime(dis.readDouble());
                rs.setDepartureTime(dis.readDouble());
                rs.setWaitingTime(dis.readDouble());
                rs.setLateness(dis.readDouble());
                rs.setCompleted(dis.readBoolean());
                routeStops.put(rs.getId(), rs);
            }

            // Traffic Events
            int eventCount = dis.readInt();
            for (int i = 0; i < eventCount; i++) {
                TrafficEventEntity te = new TrafficEventEntity();
                te.setId(dis.readUTF());
                te.setOriginId(dis.readUTF());
                te.setDestinationId(dis.readUTF());
                te.setOldMultiplier(dis.readDouble());
                te.setNewMultiplier(dis.readDouble());
                te.setTimestamp(dis.readLong());
                te.setSource(dis.readUTF());
                String aff = dis.readUTF();
                te.setAffectedOptimizationId(aff.isEmpty() ? null : aff);
                te.setProcessed(dis.readBoolean());
                trafficEvents.put(te.getId(), te);
            }

        } catch (Exception e) {
            System.err.println("Failed to read database from disk: " + e.getMessage());
        }
    }

    public DatabaseConfiguration getConfig() {
        return config;
    }
}
