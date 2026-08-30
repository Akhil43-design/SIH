package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicFleetOptimizer {

    private final RoadNetwork roadNetwork;
    private final List<Location> depots;
    private final TrafficModel trafficModel;
    private final FleetFitnessFunction fitnessFunction;
    private final TrafficConfiguration config;
    private final long optimizerSeed;
    private final List<Customer> allCustomers;

    private FleetRoutePlan activePlan;
    private final List<VehicleState> vehicleStates;
    private int reoptimizationCount;
    private double lastPreReoptFitness;
    private double lastPostReoptFitness;
    private long lastReoptimizationTimeMs;

    public DynamicFleetOptimizer(
            FleetRoutePlan initialPlan,
            List<Location> depots,
            RoadNetwork roadNetwork,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction,
            TrafficConfiguration config,
            long optimizerSeed) {

        this.activePlan = initialPlan;
        this.depots = new ArrayList<>(depots);
        this.roadNetwork = roadNetwork;
        this.trafficModel = trafficModel != null ? trafficModel : new TrafficModel();
        this.fitnessFunction = fitnessFunction != null ? fitnessFunction : new FleetFitnessFunction();
        this.config = config != null ? config : new TrafficConfiguration();
        this.optimizerSeed = optimizerSeed;
        this.reoptimizationCount = 0;

        this.allCustomers = new ArrayList<>();
        this.vehicleStates = new ArrayList<>();
        if (initialPlan != null) {
            for (VehicleRoute vr : initialPlan.getVehicleRoutes()) {
                allCustomers.addAll(vr.getCustomers());
                vehicleStates.add(new VehicleState(vr.getVehicle(), vr.getCustomers(), vr.getDepot()));
            }
        }
    }

    public boolean handleTrafficUpdate(TrafficUpdate update) {
        if (update == null || activePlan == null) {
            return false;
        }

        // 1. Threshold Check
        if (update.getRelativeChange() < config.getReoptimizationThreshold()) {
            return false; // Below significance threshold
        }

        // 2. Check if update affects any road in active plan
        boolean affectsActiveFleet = false;
        Road affectedRoad = roadNetwork.findRoad(update.getOrigin(), update.getDestination());
        if (affectedRoad == null) {
            return false;
        }

        for (VehicleRoute vr : activePlan.getVehicleRoutes()) {
            List<Location> stops = new ArrayList<>();
            stops.add(vr.getDepot());
            stops.addAll(vr.getCustomers());
            stops.add(vr.getDepot());

            for (int i = 0; i < stops.size() - 1; i++) {
                if (stops.get(i).equals(update.getOrigin()) && stops.get(i + 1).equals(update.getDestination())) {
                    affectsActiveFleet = true;
                    break;
                }
            }
            if (affectsActiveFleet) break;
        }

        if (!affectsActiveFleet) {
            return false;
        }

        // 3. Apply Traffic Update to Road Network by replacing edge
        int newTrafficLevel = Math.min(3, Math.max(1, (int) Math.round(update.getNewMultiplier())));
        Road updatedRoad = new Road(
                affectedRoad.getFrom(),
                affectedRoad.getTo(),
                affectedRoad.getDistance(),
                affectedRoad.getTravelTime(),
                affectedRoad.getFuelConsumption(),
                newTrafficLevel
        );
        roadNetwork.addRoad(updatedRoad);

        // 4. Measure Congested Plan Fitness
        FleetRoutePlan congestedPlan = reevaluatePlan(activePlan);
        this.lastPreReoptFitness = congestedPlan.getOverallFitness();

        // 5. Gather Remaining Customers across all vehicles
        List<Customer> remainingCustomers = new ArrayList<>();
        List<Vehicle> activeVehicles = new ArrayList<>();

        for (VehicleState vs : vehicleStates) {
            remainingCustomers.addAll(vs.getRemainingCustomers());
            activeVehicles.add(vs.getVehicle());
        }

        if (remainingCustomers.isEmpty()) {
            this.activePlan = congestedPlan;
            return false;
        }

        // 6. Trigger Multi-Vehicle QIGA Re-Optimization on remaining customers
        long startTime = System.currentTimeMillis();
        MultiVehicleQIGAOptimizer reoptimizer = new MultiVehicleQIGAOptimizer(
                40,
                remainingCustomers,
                activeVehicles,
                depots,
                roadNetwork,
                trafficModel,
                fitnessFunction,
                0.05,
                0.20,
                optimizerSeed + (reoptimizationCount * 31L)
        );

        FleetRoutePlan reoptimizedRemainingPlan = reoptimizer.optimize(60);
        this.lastReoptimizationTimeMs = System.currentTimeMillis() - startTime;

        // 7. Reassemble Full Fleet Plan (Completed + New Remaining)
        Map<String, List<Customer>> vehicleToReoptimizedCustomers = new HashMap<>();
        for (VehicleRoute subRoute : reoptimizedRemainingPlan.getVehicleRoutes()) {
            vehicleToReoptimizedCustomers.put(subRoute.getVehicle().getVehicleId(), subRoute.getCustomers());
        }

        List<VehicleRoute> newVehicleRoutes = new ArrayList<>();
        for (VehicleState vs : vehicleStates) {
            Vehicle v = vs.getVehicle();
            List<Customer> fullCustomerSequence = new ArrayList<>(vs.getCompletedCustomers());
            List<Customer> newRem = vehicleToReoptimizedCustomers.getOrDefault(v.getVehicleId(), Collections.emptyList());
            fullCustomerSequence.addAll(newRem);

            Location homeDepot = (v.getCurrentLocation() != null) ? v.getCurrentLocation() : depots.get(0);
            newVehicleRoutes.add(new VehicleRoute(v, fullCustomerSequence, homeDepot, roadNetwork, trafficModel));
        }

        FleetRoutePlan candidatePlan = new FleetRoutePlan(depots.get(0), newVehicleRoutes, allCustomers, fitnessFunction);

        // 8. Safety Validation
        if (isValidPlan(candidatePlan)) {
            // Update Vehicle States
            for (VehicleState vs : vehicleStates) {
                List<Customer> newRem = vehicleToReoptimizedCustomers.getOrDefault(vs.getVehicle().getVehicleId(), Collections.emptyList());
                vs.setRemainingCustomers(newRem);
            }

            this.lastPostReoptFitness = candidatePlan.getOverallFitness();
            this.activePlan = candidatePlan;
            this.reoptimizationCount++;
            return true;
        }

        this.activePlan = congestedPlan;
        return false;
    }

    private FleetRoutePlan reevaluatePlan(FleetRoutePlan plan) {
        List<VehicleRoute> updated = new ArrayList<>();
        for (VehicleRoute vr : plan.getVehicleRoutes()) {
            updated.add(new VehicleRoute(vr.getVehicle(), vr.getCustomers(), vr.getDepot(), roadNetwork, trafficModel));
        }
        return new FleetRoutePlan(depots.get(0), updated, allCustomers, fitnessFunction);
    }

    private VehicleState findStateForVehicle(Vehicle v) {
        for (VehicleState vs : vehicleStates) {
            if (vs.getVehicle().getVehicleId().equals(v.getVehicleId())) {
                return vs;
            }
        }
        return null;
    }

    private boolean isValidPlan(FleetRoutePlan plan) {
        if (plan == null) return false;
        return plan.getUnassignedCount() == 0
                && plan.getDuplicateCount() == 0
                && plan.getTotalCapacityViolations() == 0;
    }

    public FleetRoutePlan getActivePlan() {
        return activePlan;
    }

    public List<VehicleState> getVehicleStates() {
        return vehicleStates;
    }

    public int getReoptimizationCount() {
        return reoptimizationCount;
    }

    public double getLastPreReoptFitness() {
        return lastPreReoptFitness;
    }

    public double getLastPostReoptFitness() {
        return lastPostReoptFitness;
    }

    public long getLastReoptimizationTimeMs() {
        return lastReoptimizationTimeMs;
    }
}
