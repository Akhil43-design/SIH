package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClassicalGAOptimizer {

    public static class GAIndividual implements Comparable<GAIndividual> {
        List<Customer> customerOrder;
        int[] vehicleAssignments;
        FleetRoutePlan plan;
        double fitness;

        public GAIndividual(List<Customer> customerOrder, int[] vehicleAssignments) {
            this.customerOrder = new ArrayList<>(customerOrder);
            this.vehicleAssignments = vehicleAssignments.clone();
            this.fitness = Double.MAX_VALUE;
            this.plan = null;
        }

        public void evaluate(
                List<Vehicle> vehicles,
                List<Customer> allCustomers,
                Location depot,
                RoadNetwork network,
                TrafficModel trafficModel,
                FleetFitnessFunction fitnessFunction) {

            Map<Integer, List<Customer>> assignment = new HashMap<>();
            for (int v = 0; v < vehicles.size(); v++) {
                assignment.put(v, new ArrayList<>());
            }

            for (int i = 0; i < customerOrder.size(); i++) {
                Customer c = customerOrder.get(i);
                int v = vehicleAssignments[i];
                if (v < 0 || v >= vehicles.size()) {
                    v = 0;
                }
                assignment.get(v).add(c);
            }

            this.plan = MultiVehicleLocalImprover.buildPlanFromAssignment(
                    assignment, vehicles, allCustomers, depot, network, trafficModel, fitnessFunction
            );
            this.fitness = this.plan.getOverallFitness();
        }

        @Override
        public int compareTo(GAIndividual other) {
            return Double.compare(this.fitness, other.fitness);
        }
    }

    private final int populationSize;
    private final List<Customer> customers;
    private final List<Vehicle> vehicles;
    private final Location depot;
    private final RoadNetwork network;
    private final TrafficModel trafficModel;
    private final FleetFitnessFunction fitnessFunction;

    private final double crossoverRate;
    private final double mutationRate;
    private final int elitismCount;
    private final Random random;

    private FleetRoutePlan globalBestPlan;
    private double bestFitness;
    private int firstBestGeneration;
    private int lastImprovementGeneration;
    private long optimizationRuntimeMs;

    public ClassicalGAOptimizer(
            int populationSize,
            List<Customer> customers,
            List<Vehicle> vehicles,
            Location depot,
            RoadNetwork network,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction,
            double crossoverRate,
            double mutationRate,
            int elitismCount,
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

        this.populationSize = populationSize;
        this.customers = new ArrayList<>(customers);
        this.vehicles = new ArrayList<>(vehicles);
        this.depot = (depot != null) ? depot : (vehicles.get(0).getCurrentLocation() != null ? vehicles.get(0).getCurrentLocation() : new Location("W", "Depot"));
        this.network = network;
        this.trafficModel = trafficModel != null ? trafficModel : new TrafficModel();
        this.fitnessFunction = fitnessFunction != null ? fitnessFunction : new FleetFitnessFunction();
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismCount = Math.max(1, elitismCount);
        this.random = (seed != null) ? new Random(seed) : new Random();
    }

    public ClassicalGAOptimizer(
            int populationSize,
            List<Customer> customers,
            List<Vehicle> vehicles,
            Location depot,
            RoadNetwork network,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction,
            Long seed) {

        this(populationSize, customers, vehicles, depot, network, trafficModel, fitnessFunction, 0.80, 0.10, 2, seed);
    }

    public FleetRoutePlan optimize(int generations) {
        long startTime = System.nanoTime();

        bestFitness = Double.MAX_VALUE;
        globalBestPlan = null;
        firstBestGeneration = -1;
        lastImprovementGeneration = -1;

        // 1. Initialize Random Population
        List<GAIndividual> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            List<Customer> perm = new ArrayList<>(customers);
            Collections.shuffle(perm, random);

            int[] assignments = new int[customers.size()];
            for (int j = 0; j < assignments.length; j++) {
                assignments[j] = random.nextInt(vehicles.size());
            }

            GAIndividual ind = new GAIndividual(perm, assignments);
            ind.evaluate(vehicles, customers, depot, network, trafficModel, fitnessFunction);
            population.add(ind);

            if (ind.fitness < bestFitness) {
                bestFitness = ind.fitness;
                globalBestPlan = ind.plan;
                firstBestGeneration = 1;
                lastImprovementGeneration = 1;
            }
        }

        // 2. Generation Loop
        for (int gen = 1; gen <= generations; gen++) {
            Collections.sort(population);

            if (population.get(0).fitness < bestFitness) {
                bestFitness = population.get(0).fitness;
                globalBestPlan = population.get(0).plan;
                if (firstBestGeneration == -1) firstBestGeneration = gen;
                lastImprovementGeneration = gen;
            }

            List<GAIndividual> nextGen = new ArrayList<>(populationSize);

            // Elitism: carry over top elites
            for (int e = 0; e < elitismCount && e < population.size(); e++) {
                nextGen.add(new GAIndividual(population.get(e).customerOrder, population.get(e).vehicleAssignments));
            }

            // Produce offspring
            while (nextGen.size() < populationSize) {
                GAIndividual parent1 = tournamentSelect(population, 3);
                GAIndividual parent2 = tournamentSelect(population, 3);

                GAIndividual offspring = crossover(parent1, parent2);
                mutate(offspring);

                nextGen.add(offspring);
            }

            // Evaluate new generation
            for (GAIndividual ind : nextGen) {
                ind.evaluate(vehicles, customers, depot, network, trafficModel, fitnessFunction);
                if (ind.fitness < bestFitness) {
                    bestFitness = ind.fitness;
                    globalBestPlan = ind.plan;
                    lastImprovementGeneration = gen;
                }
            }

            population = nextGen;
        }

        long endTime = System.nanoTime();
        optimizationRuntimeMs = (endTime - startTime) / 1_000_000;

        return globalBestPlan;
    }

    private GAIndividual tournamentSelect(List<GAIndividual> pop, int tournamentSize) {
        GAIndividual best = null;
        for (int i = 0; i < tournamentSize; i++) {
            GAIndividual ind = pop.get(random.nextInt(pop.size()));
            if (best == null || ind.fitness < best.fitness) {
                best = ind;
            }
        }
        return best;
    }

    private GAIndividual crossover(GAIndividual p1, GAIndividual p2) {
        int n = customers.size();
        if (random.nextDouble() > crossoverRate) {
            return new GAIndividual(p1.customerOrder, p1.vehicleAssignments);
        }

        // Order Crossover (OX) for customer permutation
        int start = random.nextInt(n);
        int end = random.nextInt(n);
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        List<Customer> childPerm = new ArrayList<>(Collections.nCopies(n, (Customer) null));
        for (int i = start; i <= end; i++) {
            childPerm.set(i, p1.customerOrder.get(i));
        }

        int curP2 = 0;
        for (int i = 0; i < n; i++) {
            if (childPerm.get(i) == null) {
                while (childPerm.contains(p2.customerOrder.get(curP2))) {
                    curP2++;
                }
                childPerm.set(i, p2.customerOrder.get(curP2));
                curP2++;
            }
        }

        // Uniform Crossover for vehicle assignments
        int[] childAssign = new int[n];
        for (int i = 0; i < n; i++) {
            childAssign[i] = random.nextBoolean() ? p1.vehicleAssignments[i] : p2.vehicleAssignments[i];
        }

        return new GAIndividual(childPerm, childAssign);
    }

    private void mutate(GAIndividual ind) {
        int n = customers.size();

        // Permutation swap mutation
        if (random.nextDouble() < mutationRate) {
            int i = random.nextInt(n);
            int j = random.nextInt(n);
            Customer tmp = ind.customerOrder.get(i);
            ind.customerOrder.set(i, ind.customerOrder.get(j));
            ind.customerOrder.set(j, tmp);
        }

        // Vehicle assignment mutation
        if (random.nextDouble() < mutationRate) {
            int idx = random.nextInt(n);
            ind.vehicleAssignments[idx] = random.nextInt(vehicles.size());
        }
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

    public long getOptimizationRuntimeMs() {
        return optimizationRuntimeMs;
    }
}
