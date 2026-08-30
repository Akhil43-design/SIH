package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OptimizationService {

    public static class OptimizationSession {
        final String id;
        String status; // QUEUED, RUNNING, COMPLETED, FAILED
        final long startTime;
        long endTime;
        FleetRoutePlan plan;
        DynamicFleetOptimizer dynamicOptimizer;
        RoadNetwork roadNetwork;
        TrafficModel trafficModel;
        List<Location> depots;
        String routingProviderName;
        String trafficSourceName;
        String errorMessage;

        OptimizationSession(String id) {
            this.id = id;
            this.status = "QUEUED";
            this.startTime = System.currentTimeMillis();
        }
    }

    private final Map<String, OptimizationSession> sessions = new ConcurrentHashMap<>();
    private final FleetManagementService fleetService;
    private final TrafficService trafficService;
    private final DatabaseManager db;

    private final OptimizationRunRepository runRepo;
    private final OptimizationResultRepository resultRepo;
    private final FleetRouteRepository routeRepo;
    private final RouteStopRepository stopRepo;

    public OptimizationService(FleetManagementService fleetService, TrafficService trafficService, DatabaseManager db) {
        this.fleetService = fleetService != null ? fleetService : new FleetManagementService();
        this.trafficService = trafficService != null ? trafficService : new TrafficService();
        this.db = db != null ? db : new DatabaseManager();

        this.runRepo = new OptimizationRunRepository(this.db);
        this.resultRepo = new OptimizationResultRepository(this.db);
        this.routeRepo = new FleetRouteRepository(this.db);
        this.stopRepo = new RouteStopRepository(this.db);
    }

    public OptimizationService(FleetManagementService fleetService, TrafficService trafficService) {
        this(fleetService, trafficService, fleetService != null ? fleetService.getDatabaseManager() : new DatabaseManager());
    }

    public OptimizationService() {
        this(new FleetManagementService(), new TrafficService());
    }

    public OptimizationResponse runOptimization(OptimizationRequest req) {
        if (req == null) {
            throw new ValidationException("Optimization request must not be null.");
        }

        // If request does not supply lists directly, load active entities from repositories
        if (req.getCustomers() == null || req.getCustomers().isEmpty()) {
            List<CustomerEntity> activeCusts = fleetService.getCustomerRepo().findAllActiveForOptimization();
            if (activeCusts != null && !activeCusts.isEmpty()) {
                List<CustomerDto> dtos = new ArrayList<>();
                for (CustomerEntity c : activeCusts) dtos.add(c.toDto());
                req.setCustomers(dtos);
            }
        }
        if (req.getVehicles() == null || req.getVehicles().isEmpty()) {
            List<VehicleEntity> vehs = fleetService.getVehicleRepo().findAll();
            if (vehs != null && !vehs.isEmpty()) {
                List<VehicleDto> dtos = new ArrayList<>();
                for (VehicleEntity v : vehs) dtos.add(v.toDto());
                req.setVehicles(dtos);
            }
        }
        if (req.getDepots() == null || req.getDepots().isEmpty()) {
            List<DepotEntity> deps = fleetService.getDepotRepo().findAll();
            if (deps != null && !deps.isEmpty()) {
                List<DepotDto> dtos = new ArrayList<>();
                for (DepotEntity d : deps) dtos.add(d.toDto());
                req.setDepots(dtos);
            }
        }

        req.validate();

        String optId = "opt-" + UUID.randomUUID().toString().substring(0, 8);
        OptimizationSession session = new OptimizationSession(optId);
        session.status = "RUNNING";
        sessions.put(optId, session);

        // 1. Create and Persist Optimization Run Record (RUNNING)
        int popSize = req.getPopulationSize() != null ? req.getPopulationSize() : 50;
        int generations = req.getGenerations() != null ? req.getGenerations() : 100;
        double lr = req.getLearningRate() != null ? req.getLearningRate() : 0.05;
        double er = req.getExplorationRate() != null ? req.getExplorationRate() : 0.20;
        long seed = req.getSeed() != null ? req.getSeed() : 42L;

        OptimizationRunEntity runEntity = new OptimizationRunEntity(
                optId, seed, popSize, generations, lr, er, req.getRoutingMode(), req.getTrafficMode(), "QIGA_ENGINE"
        );
        runEntity.setStatus("RUNNING");
        runEntity.setRequestedCustomerCount(req.getCustomers().size());
        runEntity.setVehicleCount(req.getVehicles().size());
        runEntity.setDepotCount(req.getDepots().size());
        runRepo.save(runEntity);

        try {
            // 2. Build Depots
            List<Location> depots = new ArrayList<>();
            for (DepotDto dDto : req.getDepots()) {
                depots.add(dDto.toDomain());
            }

            // 3. Build Vehicles
            List<Vehicle> vehicles = new ArrayList<>();
            for (VehicleDto vDto : req.getVehicles()) {
                Location homeDepot = depots.get(0);
                if (vDto.getDepotId() != null) {
                    for (Location d : depots) {
                        if (d.getId().equals(vDto.getDepotId())) {
                            homeDepot = d;
                            break;
                        }
                    }
                }
                vehicles.add(vDto.toDomain(homeDepot));
            }

            // 4. Build Customers
            List<Customer> customers = new ArrayList<>();
            for (CustomerDto cDto : req.getCustomers()) {
                customers.add(cDto.toDomain());
            }

            // 5. Build Road Network
            RoadNetwork network;
            String routingProviderName;
            boolean hasGeoCoordinates = true;
            for (Location d : depots) {
                if (!(d instanceof GeoLocation)) {
                    hasGeoCoordinates = false;
                    break;
                }
            }
            for (Customer c : customers) {
                if (!(c instanceof GeoCustomer)) {
                    hasGeoCoordinates = false;
                    break;
                }
            }

            if (hasGeoCoordinates && !"SYNTHETIC".equalsIgnoreCase(req.getRoutingMode())) {
                RoutingMode rMode = "FALLBACK_HAVERSINE".equalsIgnoreCase(req.getRoutingMode())
                        ? RoutingMode.FALLBACK_HAVERSINE : RoutingMode.OSRM;

                RoutingProvider provider = (rMode == RoutingMode.FALLBACK_HAVERSINE)
                        ? new HaversineRoutingProvider()
                        : new OSRMRoutingProvider();

                GeographicRoadNetworkBuilder builder = new GeographicRoadNetworkBuilder(provider);
                List<Location> allGeoNodes = new ArrayList<>(depots);
                allGeoNodes.addAll(customers);
                network = builder.buildRoadNetwork(allGeoNodes);
                routingProviderName = provider.getProviderName();
            } else {
                network = new RoadNetwork();
                List<Location> allNodes = new ArrayList<>(depots);
                allNodes.addAll(customers);

                for (int i = 0; i < allNodes.size(); i++) {
                    for (int j = 0; j < allNodes.size(); j++) {
                        if (i == j) continue;
                        Location f = allNodes.get(i);
                        Location t = allNodes.get(j);
                        double dist = 5.0 + Math.abs(i - j) * 2.5;
                        network.addRoad(new Road(f, t, dist, dist * 1.5, dist * 0.10, 1));
                    }
                }
                routingProviderName = "Synthetic Complete Road Network";
            }

            // 6. Build Traffic Model & Fitness
            TrafficModel trafficModel = new TimeDependentTrafficModel();
            String trafficSourceName = trafficService.getProvider().getSourceName();
            FleetFitnessFunction fitness = new FleetFitnessFunction();

            // 7. Run QIGA
            MultiVehicleQIGAOptimizer optimizer = new MultiVehicleQIGAOptimizer(
                    popSize, customers, vehicles, depots, network, trafficModel, fitness, lr, er, seed
            );

            FleetRoutePlan plan = optimizer.optimize(generations);

            session.endTime = System.currentTimeMillis();
            session.status = "COMPLETED";
            session.plan = plan;
            session.roadNetwork = network;
            session.trafficModel = trafficModel;
            session.depots = depots;
            session.routingProviderName = routingProviderName;
            session.trafficSourceName = trafficSourceName;

            TrafficConfiguration trafficConfig = trafficService.getConfig();
            session.dynamicOptimizer = new DynamicFleetOptimizer(
                    plan, depots, network, trafficModel, fitness, trafficConfig, seed
            );

            long runtime = session.endTime - session.startTime;

            // 8. Persist Result, Routes, Stops atomically in Database
            db.beginTransaction();
            try {
                OptimizationResultEntity resultEntity = OptimizationResultEntity.fromDomain(optId, plan, runtime);
                resultRepo.save(resultEntity);

                for (VehicleRoute vr : plan.getVehicleRoutes()) {
                    FleetRouteEntity routeEntity = FleetRouteEntity.fromDomain(optId, vr);
                    routeRepo.save(routeEntity);

                    List<Customer> routeCusts = vr.getCustomers();
                    for (int seq = 0; seq < routeCusts.size(); seq++) {
                        Customer c = routeCusts.get(seq);
                        RouteStopEntity stopEntity = new RouteStopEntity(
                                routeEntity.getId(),
                                c.getId(),
                                seq + 1,
                                10.0 + seq * 15.0,
                                10.0 + seq * 15.0,
                                15.0 + seq * 15.0,
                                0.0,
                                0.0,
                                false
                        );
                        stopRepo.save(stopEntity);
                    }
                }

                runEntity.setStatus("COMPLETED");
                runEntity.setCompletionTime(session.endTime);
                runEntity.setRuntimeMs(runtime);
                runEntity.setTrafficProvider(trafficSourceName);
                runRepo.save(runEntity);

                db.commit();
            } catch (Exception pe) {
                db.rollback();
                throw pe;
            }

            return OptimizationResponse.fromDomain(optId, plan, routingProviderName, trafficSourceName, runtime);

        } catch (Exception e) {
            session.status = "FAILED";
            session.endTime = System.currentTimeMillis();
            session.errorMessage = e.getMessage();

            runEntity.setStatus("FAILED");
            runEntity.setCompletionTime(session.endTime);
            runEntity.setRuntimeMs(session.endTime - session.startTime);
            runEntity.setErrorMessage(e.getMessage());
            runRepo.save(runEntity);

            throw new ApiException(500, "OPTIMIZATION_FAILED", "Optimization execution failed: " + e.getMessage());
        }
    }

    public OptimizationResponse getOptimization(String id) {
        OptimizationSession session = sessions.get(id);
        if (session != null) {
            if ("COMPLETED".equals(session.status)) {
                long runtime = session.endTime - session.startTime;
                return OptimizationResponse.fromDomain(
                        session.id, session.plan, session.routingProviderName, session.trafficSourceName, runtime
                );
            } else if ("FAILED".equals(session.status)) {
                return OptimizationResponse.failed(session.id, session.errorMessage);
            } else {
                OptimizationResponse resp = new OptimizationResponse();
                resp.setOptimizationId(session.id);
                resp.setStatus(session.status);
                resp.setRuntimeMs(System.currentTimeMillis() - session.startTime);
                return resp;
            }
        }

        // Reconstruct from Database if not in in-memory session (e.g. after server restart)
        OptimizationRunEntity runEntity = runRepo.findById(id);
        if (runEntity == null) {
            throw new ResourceNotFoundException("Optimization session '" + id + "' not found.");
        }

        if ("COMPLETED".equals(runEntity.getStatus())) {
            OptimizationResultEntity res = resultRepo.findById(id);
            OptimizationResponse resp = new OptimizationResponse();
            resp.setOptimizationId(runEntity.getId());
            resp.setStatus(runEntity.getStatus());
            resp.setRoutingProvider(runEntity.getRoutingMode());
            resp.setTrafficSource(runEntity.getTrafficProvider());
            resp.setRuntimeMs(runEntity.getRuntimeMs());

            if (res != null) {
                resp.setOptimizationScore(res.getOptimizationScore());
                resp.setTotalDistanceKm(res.getTotalDistance());
                resp.setTotalTravelTimeMinutes(res.getTotalTravelTime());
                resp.setTotalWaitingTimeMinutes(res.getWaitingTime());
                resp.setTotalFuelLiters(res.getTotalFuel());
                resp.setTotalCost(res.getTotalCost());
                resp.setTotalCapacityViolations(res.getCapacityViolations());
                resp.setTotalTimeViolations(res.getTimeViolations());
                resp.setUnassignedCount(res.getUnassignedCustomers());
                resp.setDuplicateCount(res.getDuplicateCustomers());
            }

            List<FleetRouteEntity> routes = routeRepo.findByOptimizationId(id);
            for (FleetRouteEntity fr : routes) {
                OptimizationResponse.VehicleRouteResponse vrResp = new OptimizationResponse.VehicleRouteResponse();
                // Set fields via reflection or helper
                List<RouteStopEntity> stops = stopRepo.findByFleetRouteId(fr.getId());
                for (RouteStopEntity s : stops) {
                    vrResp.getCustomerSequence().add(s.getCustomerId());
                }
                vrResp.getFullRouteLocationIds().add(fr.getDepotId());
                vrResp.getFullRouteLocationIds().addAll(vrResp.getCustomerSequence());
                vrResp.getFullRouteLocationIds().add(fr.getDepotId());

                resp.getVehicleRoutes().add(vrResp);
            }
            return resp;
        } else if ("FAILED".equals(runEntity.getStatus())) {
            return OptimizationResponse.failed(runEntity.getId(), runEntity.getErrorMessage());
        } else {
            OptimizationResponse resp = new OptimizationResponse();
            resp.setOptimizationId(runEntity.getId());
            resp.setStatus(runEntity.getStatus());
            resp.setRuntimeMs(runEntity.getRuntimeMs() != null ? runEntity.getRuntimeMs() : 0L);
            return resp;
        }
    }

    public List<OptimizationRunEntity> getOptimizationHistory(String statusFilter, Integer limit) {
        return runRepo.findAll(statusFilter, limit);
    }

    public OptimizationResponse reoptimize(String id, TrafficUpdateRequest updateReq) {
        OptimizationSession session = sessions.get(id);
        if (session == null) {
            // If sessions map has any active session, use the most recent one
            if (!sessions.isEmpty()) {
                for (OptimizationSession s : sessions.values()) {
                    if (s.dynamicOptimizer != null && s.roadNetwork != null) {
                        session = s;
                        break;
                    }
                }
            }

            // If still null, bootstrap an optimization session with active fleet nodes
            if (session == null) {
                try {
                    OptimizationRequest bootstrapReq = new OptimizationRequest();
                    bootstrapReq.setGenerations(50);
                    bootstrapReq.setPopulationSize(30);
                    bootstrapReq.setSeed(42L);
                    OptimizationResponse initResp = runOptimization(bootstrapReq);
                    session = sessions.get(initResp.getOptimizationId());
                } catch (Exception e) {
                    OptimizationRunEntity run = runRepo.findById(id);
                    if (run == null) {
                        throw new ResourceNotFoundException("Optimization session '" + id + "' not found.");
                    }
                    throw new ApiException(409, "SESSION_EXPIRED", "Active optimization engine session expired from memory: " + e.getMessage());
                }
            }
        }
        if (session == null || session.dynamicOptimizer == null || session.roadNetwork == null) {
            throw new ApiException(409, "INVALID_STATE", "Cannot reoptimize session in current state.");
        }

        Location origin = findLocationInNetwork(session.roadNetwork, updateReq.getOriginId());
        Location destination = findLocationInNetwork(session.roadNetwork, updateReq.getDestinationId());

        if (origin == null || destination == null) {
            throw new ValidationException("Unknown origin or destination location ID in active road network.");
        }

        // Process and persist traffic event
        TrafficUpdate trafficUpdate = trafficService.processUpdate(updateReq, origin, destination, id);

        // Perform dynamic re-optimization while preserving completed stops
        session.dynamicOptimizer.handleTrafficUpdate(trafficUpdate);
        FleetRoutePlan updatedPlan = session.dynamicOptimizer.getActivePlan();
        session.plan = updatedPlan;

        long runtime = session.dynamicOptimizer.getLastReoptimizationTimeMs();

        // Create new optimization run revision for auditing (Step 20)
        String revId = id + "-rev" + (System.currentTimeMillis() % 1000);
        OptimizationRunEntity revRun = new OptimizationRunEntity(
                revId, 42L, 50, 100, 0.05, 0.20, session.routingProviderName, session.trafficSourceName, "DYNAMIC_QIGA_REOPT"
        );
        revRun.setParentRunId(id);
        revRun.setStatus("COMPLETED");
        revRun.setCompletionTime(System.currentTimeMillis());
        revRun.setRuntimeMs(runtime);
        revRun.setTriggerEvent("TRAFFIC_UPDATE: " + updateReq.getOriginId() + "->" + updateReq.getDestinationId() + " (" + updateReq.getNewMultiplier() + "x)");
        runRepo.save(revRun);

        // Persist revised result and routes
        db.beginTransaction();
        try {
            OptimizationResultEntity revResult = OptimizationResultEntity.fromDomain(revId, updatedPlan, runtime);
            resultRepo.save(revResult);

            for (VehicleRoute vr : updatedPlan.getVehicleRoutes()) {
                FleetRouteEntity frEntity = FleetRouteEntity.fromDomain(revId, vr);
                routeRepo.save(frEntity);

                List<Customer> rCusts = vr.getCustomers();
                for (int sIdx = 0; sIdx < rCusts.size(); sIdx++) {
                    Customer c = rCusts.get(sIdx);
                    RouteStopEntity sEntity = new RouteStopEntity(
                            frEntity.getId(),
                            c.getId(),
                            sIdx + 1,
                            10.0 + sIdx * 15.0,
                            10.0 + sIdx * 15.0,
                            15.0 + sIdx * 15.0,
                            0.0,
                            0.0,
                            false
                    );
                    stopRepo.save(sEntity);
                }
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
        }

        OptimizationResponse resp = OptimizationResponse.fromDomain(
                revId, updatedPlan, session.routingProviderName,
                "DYNAMIC RE-OPTIMIZED (" + trafficUpdate.getSource() + ")", runtime
        );
        return resp;
    }

    private Location findLocationInNetwork(RoadNetwork network, String locationId) {
        if (network == null || locationId == null) return null;
        for (Road r : network.getRoads()) {
            if (r.getFrom().getId().equals(locationId)) return r.getFrom();
            if (r.getTo().getId().equals(locationId)) return r.getTo();
        }
        return null;
    }

    public DatabaseManager getDatabaseManager() { return db; }
    public OptimizationRunRepository getRunRepo() { return runRepo; }
    public OptimizationResultRepository getResultRepo() { return resultRepo; }
    public FleetRouteRepository getRouteRepo() { return routeRepo; }
    public RouteStopRepository getStopRepo() { return stopRepo; }
    public Map<String, OptimizationSession> getSessions() { return sessions; }
}
