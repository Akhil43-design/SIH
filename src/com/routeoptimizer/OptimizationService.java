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

    public OptimizationService(FleetManagementService fleetService, TrafficService trafficService) {
        this.fleetService = fleetService != null ? fleetService : new FleetManagementService();
        this.trafficService = trafficService != null ? trafficService : new TrafficService();
    }

    public OptimizationService() {
        this(new FleetManagementService(), new TrafficService());
    }

    public OptimizationResponse runOptimization(OptimizationRequest req) {
        if (req == null) {
            throw new ValidationException("Optimization request must not be null.");
        }
        req.validate();

        String optId = "opt-" + UUID.randomUUID().toString().substring(0, 8);
        OptimizationSession session = new OptimizationSession(optId);
        session.status = "RUNNING";
        sessions.put(optId, session);

        try {
            // 1. Build Depots
            List<Location> depots = new ArrayList<>();
            for (DepotDto dDto : req.getDepots()) {
                depots.add(dDto.toDomain());
            }

            // 2. Build Vehicles
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

            // 3. Build Customers
            List<Customer> customers = new ArrayList<>();
            for (CustomerDto cDto : req.getCustomers()) {
                customers.add(cDto.toDomain());
            }

            // 4. Build Road Network based on Routing Mode & Coordinates
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

            // 5. Build Traffic Model
            TrafficModel trafficModel = new TimeDependentTrafficModel();
            String trafficSourceName = trafficService.getProvider().getSourceName();

            // 6. Multi-Objective Fitness
            FleetFitnessFunction fitness = new FleetFitnessFunction();

            // 7. Run QIGA
            int popSize = req.getPopulationSize() != null ? req.getPopulationSize() : 50;
            int generations = req.getGenerations() != null ? req.getGenerations() : 100;
            double lr = req.getLearningRate() != null ? req.getLearningRate() : 0.05;
            double er = req.getExplorationRate() != null ? req.getExplorationRate() : 0.20;
            long seed = req.getSeed() != null ? req.getSeed() : 42L;

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
            return OptimizationResponse.fromDomain(optId, plan, routingProviderName, trafficSourceName, runtime);

        } catch (Exception e) {
            session.status = "FAILED";
            session.endTime = System.currentTimeMillis();
            session.errorMessage = e.getMessage();
            throw new ApiException(500, "OPTIMIZATION_FAILED", "Optimization execution failed: " + e.getMessage());
        }
    }

    public OptimizationResponse getOptimization(String id) {
        OptimizationSession session = sessions.get(id);
        if (session == null) {
            throw new ResourceNotFoundException("Optimization session '" + id + "' not found.");
        }

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

    public OptimizationResponse reoptimize(String id, TrafficUpdateRequest updateReq) {
        OptimizationSession session = sessions.get(id);
        if (session == null) {
            throw new ResourceNotFoundException("Optimization session '" + id + "' not found.");
        }
        if (session.dynamicOptimizer == null || session.roadNetwork == null) {
            throw new ApiException(409, "INVALID_STATE", "Cannot reoptimize session in status: " + session.status);
        }

        Location origin = findLocationInNetwork(session.roadNetwork, updateReq.getOriginId());
        Location destination = findLocationInNetwork(session.roadNetwork, updateReq.getDestinationId());

        if (origin == null || destination == null) {
            throw new ValidationException("Unknown origin or destination location ID in active road network.");
        }

        TrafficUpdate trafficUpdate = trafficService.processUpdate(updateReq, origin, destination);
        session.dynamicOptimizer.handleTrafficUpdate(trafficUpdate);

        FleetRoutePlan updatedPlan = session.dynamicOptimizer.getActivePlan();
        session.plan = updatedPlan;

        long runtime = session.dynamicOptimizer.getLastReoptimizationTimeMs();
        OptimizationResponse resp = OptimizationResponse.fromDomain(
                session.id, updatedPlan, session.routingProviderName,
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

    public Map<String, OptimizationSession> getSessions() {
        return sessions;
    }
}
