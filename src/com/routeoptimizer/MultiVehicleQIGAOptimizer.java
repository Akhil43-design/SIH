package com.routeoptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MultiVehicleQIGAOptimizer {

    private final int populationSize;
    private final List<Customer> customers;
    private final List<Vehicle> vehicles;
    private final Location depot;
    private final RoadNetwork network;
    private final TrafficModel trafficModel;
    private final FleetFitnessFunction fitnessFunction;

    private final double learningRate;
    private double explorationRate;
    private final double initialExplorationRate;
    private final double minExplorationRate = 0.03;
    private final double maxExplorationRate = 0.30;
    private final double explorationDecay = 0.96;

    private final Random random;
    private final List<FleetQuantumIndividual> population;

    private FleetRoutePlan globalBestPlan;
    private double bestFitness;

    private int firstBestGeneration;
    private int lastImprovementGeneration;
    private int totalImprovements;
    private int solutionsImprovedCount;
    private long optimizationRuntimeMs;
    private long localSearchRuntimeMs;

    public MultiVehicleQIGAOptimizer(
            int populationSize,
            List<Customer> customers,
            List<Vehicle> vehicles,
            Location depot,
            RoadNetwork network,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction,
            double learningRate,
            double explorationRate,
            Long seed) {

        if (populationSize <= 0) {
            throw new IllegalArgumentException("Population size must be greater than 0.");
        }
        if (customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException("Customer list cannot be null or empty.");
        }
        if (vehicles == null || vehicles.isEmpty()) {
            throw new IllegalArgumentException("Vehicle fleet cannot be null or empty.");
        }
        if (depot == null) {
            throw new IllegalArgumentException("Depot cannot be null.");
        }
        if (network == null) {
            throw new IllegalArgumentException("Road network cannot be null.");
        }

        this.populationSize = populationSize;
        this.customers = new ArrayList<>(customers);
        this.vehicles = new ArrayList<>(vehicles);
        this.depot = depot;
        this.network = network;
        this.trafficModel = trafficModel != null ? trafficModel : new TrafficModel();
        this.fitnessFunction = fitnessFunction != null ? fitnessFunction : new FleetFitnessFunction();
        this.learningRate = learningRate;
        this.explorationRate = explorationRate;
        this.initialExplorationRate = explorationRate;

        this.random = (seed != null) ? new Random(seed) : new Random();

        this.population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            this.population.add(new FleetQuantumIndividual(customers.size(), vehicles.size()));
        }

        this.bestFitness = Double.MAX_VALUE;
        this.globalBestPlan = null;
    }

    public MultiVehicleQIGAOptimizer(
            int populationSize,
            List<Customer> customers,
            List<Vehicle> vehicles,
            Location depot,
            RoadNetwork network,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction) {

        this(populationSize, customers, vehicles, depot, network, trafficModel, fitnessFunction, 0.05, 0.20, null);
    }

    public FleetRoutePlan optimize(int generations) {
        long startTime = System.nanoTime();

        bestFitness = Double.MAX_VALUE;
        globalBestPlan = null;
        firstBestGeneration = -1;
        lastImprovementGeneration = -1;
        totalImprovements = 0;
        solutionsImprovedCount = 0;
        localSearchRuntimeMs = 0;

        explorationRate = initialExplorationRate;
        int generationsWithoutImprovement = 0;

        for (int gen = 1; gen <= generations; gen++) {
            boolean genImproved = false;

            for (FleetQuantumIndividual individual : population) {
                // 1. Generate customer permutation and vehicle assignment
                List<Customer> perm = individual.generateCustomerPermutation(customers, random, explorationRate);
                Map<Integer, List<Customer>> assignment = individual.generateFleetAssignment(perm, customers, random, explorationRate);

                // 2. Build initial fleet plan
                FleetRoutePlan rawPlan = MultiVehicleLocalImprover.buildPlanFromAssignment(
                        assignment, vehicles, customers, depot, network, trafficModel, fitnessFunction
                );

                // 3. Apply Multi-Vehicle Local Improvement
                long lsStart = System.nanoTime();
                MultiVehicleLocalImprover.FleetImprovementResult lsResult = MultiVehicleLocalImprover.improveFleetPlan(
                        rawPlan, vehicles, customers, depot, network, trafficModel, fitnessFunction, 2
                );
                long lsEnd = System.nanoTime();
                localSearchRuntimeMs += (lsEnd - lsStart) / 1_000_000;

                FleetRoutePlan evaluatedPlan = lsResult.getPlan();
                if (lsResult.getImprovementCount() > 0) {
                    solutionsImprovedCount++;
                    totalImprovements += lsResult.getImprovementCount();
                }

                // 4. Update Global Best
                double fit = evaluatedPlan.getOverallFitness();
                if (fit < bestFitness) {
                    bestFitness = fit;
                    globalBestPlan = evaluatedPlan;
                    genImproved = true;
                    if (firstBestGeneration == -1) {
                        firstBestGeneration = gen;
                    }
                    lastImprovementGeneration = gen;
                }
            }

            if (genImproved) {
                generationsWithoutImprovement = 0;
            } else {
                generationsWithoutImprovement++;
            }

            // 5. Update Quantum Population towards Global Best Plan
            if (globalBestPlan != null) {
                List<Customer> bestOrderedPerm = extractOrderedPermutation(globalBestPlan);
                Map<Customer, Integer> bestCustVehicleMap = extractCustomerVehicleMap(globalBestPlan);

                for (FleetQuantumIndividual individual : population) {
                    individual.update(bestOrderedPerm, bestCustVehicleMap, customers, learningRate);
                }
            }

            // 6. Adaptive Exploration Rate Decay & Stagnation handling
            if (generationsWithoutImprovement >= 5) {
                explorationRate = Math.min(maxExplorationRate, explorationRate + 0.05);
            } else {
                explorationRate = Math.max(minExplorationRate, explorationRate * explorationDecay);
            }
        }

        long endTime = System.nanoTime();
        optimizationRuntimeMs = (endTime - startTime) / 1_000_000;

        return globalBestPlan;
    }

    private List<Customer> extractOrderedPermutation(FleetRoutePlan plan) {
        List<Customer> perm = new ArrayList<>();
        for (VehicleRoute vr : plan.getVehicleRoutes()) {
            perm.addAll(vr.getCustomers());
        }
        return perm;
    }

    private Map<Customer, Integer> extractCustomerVehicleMap(FleetRoutePlan plan) {
        Map<Customer, Integer> map = new HashMap<>();
        List<VehicleRoute> vRoutes = plan.getVehicleRoutes();
        for (int v = 0; v < vRoutes.size(); v++) {
            for (Customer c : vRoutes.get(v).getCustomers()) {
                map.put(c, v);
            }
        }
        return map;
    }

    public FleetRoutePlan getGlobalBestPlan() {
        return globalBestPlan;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public int getFirstBestGeneration() {
        return firstBestGeneration;
    }

    public int getLastImprovementGeneration() {
        return lastImprovementGeneration;
    }

    public int getTotalImprovements() {
        return totalImprovements;
    }

    public int getSolutionsImprovedCount() {
        return solutionsImprovedCount;
    }

    public long getOptimizationRuntimeMs() {
        return optimizationRuntimeMs;
    }

    public long getLocalSearchRuntimeMs() {
        return localSearchRuntimeMs;
    }
}
