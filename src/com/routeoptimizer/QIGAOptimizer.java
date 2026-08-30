package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class QIGAOptimizer {

    // ============================================
    // QIGA COMPONENTS
    // ============================================

    private final QuantumPositionPopulation population;

    private final QuantumPopulationEvaluator evaluator;

    private final PositionProbabilityUpdater updater;

    private final List<Location> customers;

    private final BestSolution globalBest;


    // ============================================
    // CONVERGENCE INFORMATION
    // ============================================

    private int generationsWithoutImprovement = 0;

    private int firstBestGeneration = -1;

    private int lastImprovementGeneration = -1;


    // ============================================
    // LOCAL SEARCH INFORMATION
    // ============================================

    private int totalRoutesEvaluated = 0;

    private int routesImproved = 0;

    private int totalImprovements = 0;

    private double totalCostImprovement = 0.0;


    // ============================================
    // ADAPTIVE EXPLORATION PARAMETERS
    // ============================================

    /*
     * Initial exploration rate.
     *
     * At the beginning we want more exploration
     * because the algorithm does not know which
     * routes are good.
     */
    private static final double MAX_EXPLORATION = 0.30;


    /*
     * Minimum exploration rate.
     *
     * Later the algorithm concentrates more on
     * the learned probability distribution.
     */
    private static final double MIN_EXPLORATION = 0.03;


    /*
     * Number of generations over which exploration
     * decreases from maximum to minimum.
     */
    private static final int DECAY_GENERATIONS = 50;


    /*
     * If the algorithm does not improve for this
     * many generations, additional exploration
     * is activated.
     */
    private static final int STAGNATION_THRESHOLD = 5;


    /*
     * Maximum exploration used during stagnation.
     */
    private static final double STAGNATION_EXPLORATION = 0.18;


    /*
     * Additional exploration added when stagnating.
     */
    private static final double STAGNATION_BOOST = 0.08;


    // ============================================
    // CONSTRUCTOR
    // ============================================

    public QIGAOptimizer(
            int populationSize,
            List<Location> customers,
            QuantumPopulationEvaluator evaluator,
            PositionProbabilityUpdater updater) {

        if (populationSize <= 0) {

            throw new IllegalArgumentException(
                    "Population size must be greater than 0."
            );
        }

        if (customers == null ||
                customers.isEmpty()) {

            throw new IllegalArgumentException(
                    "Customers cannot be empty."
            );
        }

        if (evaluator == null) {

            throw new IllegalArgumentException(
                    "Evaluator cannot be null."
            );
        }

        if (updater == null) {

            throw new IllegalArgumentException(
                    "Updater cannot be null."
            );
        }


        /*
         * Create quantum population.
         */
        this.population =
                new QuantumPositionPopulation(
                        populationSize,
                        customers.size()
                );


        /*
         * Copy customer list.
         */
        this.customers =
                new ArrayList<>(customers);


        this.evaluator = evaluator;

        this.updater = updater;


        /*
         * Global best solution.
         */
        this.globalBest =
                new BestSolution();


        /*
         * Start with high exploration.
         */
        QuantumPositionRouteGenerator
                .setExplorationRate(
                        MAX_EXPLORATION
                );
    }


    // ============================================
    // OPTIMIZATION
    // ============================================

    public void optimize(int generations) {

        if (generations <= 0) {

            throw new IllegalArgumentException(
                    "Number of generations must be greater than 0."
            );
        }

        firstBestGeneration = -1;
        lastImprovementGeneration = -1;

        totalRoutesEvaluated = 0;
        routesImproved = 0;
        totalImprovements = 0;
        totalCostImprovement = 0.0;


        /*
         * =========================================
         * GENERATION LOOP
         * =========================================
         */

        for (int generation = 1;
             generation <= generations;
             generation++) {


            // -------------------------------------
            // Store previous global best
            // -------------------------------------

            double previousGlobalBest =
                    globalBest.getCost();


            // -------------------------------------
            // Generation best
            // -------------------------------------

            double generationBestCost =
                    Double.MAX_VALUE;

            List<Location> generationBestRoute =
                    null;


            // -------------------------------------
            // Update exploration rate
            // -------------------------------------

            updateExplorationRate(
                    generation,
                    generations
            );


            // -------------------------------------
            // Create statistics object
            // -------------------------------------

            RouteStatistics statistics =
                    new RouteStatistics();


            // =====================================
            // STEP 1
            // GENERATE AND EVALUATE POPULATION
            // =====================================

            for (List<PositionQBit> individual :
                    population.getIndividuals()) {


                /*
                 * Generate a customer permutation.
                 */
                List<Location> rawRoute =
                        QuantumPositionRouteGenerator
                                .generateRoute(
                                        individual,
                                        customers
                                );


                /*
                 * Build complete route:
                 *
                 * W -> Customer 1
                 * -> Customer 2
                 * -> ...
                 * -> W
                 */
                Route completeRoute =
                        evaluator.buildRouteFromOrder(
                                rawRoute
                        );


                /*
                 * Calculate initial route cost.
                 */
                double initialCost =
                        evaluator.calculateCost(
                                completeRoute
                        );

                totalRoutesEvaluated++;


                /*
                 * Apply lightweight local route improvement.
                 */
                LocalRouteImprover.ImprovementResult improvementResult =
                        LocalRouteImprover.improveWithDetails(
                                rawRoute,
                                evaluator.getWarehouse(),
                                evaluator.getNetwork(),
                                evaluator.getFitnessFunction()
                        );

                List<Location> route =
                        improvementResult.getRoute();

                double cost =
                        improvementResult.getCost();

                if (cost < initialCost - 1e-9) {

                    routesImproved++;

                    totalImprovements +=
                            improvementResult.getImprovementCount();

                    totalCostImprovement +=
                            (initialCost - cost);
                }


                /*
                 * Store route statistics.
                 */
                statistics.add(
                        route,
                        cost
                );


                /*
                 * Check generation best.
                 */
                if (cost < generationBestCost) {

                    generationBestCost =
                            cost;

                    generationBestRoute =
                            new ArrayList<>(
                                    route
                            );
                }


                /*
                 * Check global best.
                 */
                globalBest.update(
                        route,
                        cost
                );
            }


            // =====================================
            // STEP 2
            // CHECK GLOBAL IMPROVEMENT
            // =====================================

            boolean improved =
                    globalBest.getCost()
                            < previousGlobalBest;


            if (improved) {

                generationsWithoutImprovement = 0;

                if (firstBestGeneration == -1) {
                    firstBestGeneration = generation;
                }

                lastImprovementGeneration = generation;

            } else {

                generationsWithoutImprovement++;
            }


            // =====================================
            // STEP 3
            // UPDATE QUANTUM PROBABILITIES
            // =====================================

            if (globalBest.exists()) {

                for (List<PositionQBit> individual :
                        population.getIndividuals()) {


                    updater.update(
                            individual,
                            globalBest.getRoute(),
                            customers
                    );
                }
            }


            // =====================================
            // STEP 4
            // HANDLE STAGNATION
            // =====================================

            if (generationsWithoutImprovement
                    >= STAGNATION_THRESHOLD) {


                double currentRate =
                        QuantumPositionRouteGenerator
                                .getExplorationRate();


                double boostedRate =
                        Math.min(
                                STAGNATION_EXPLORATION,
                                currentRate
                                        + STAGNATION_BOOST
                        );


                QuantumPositionRouteGenerator
                        .setExplorationRate(
                                boostedRate
                        );
            }


            // =====================================
            // STEP 5
            // DISPLAY STATISTICS
            // =====================================

            System.out.println(
                    "Generation "
                            + generation
                            + " | Best: "
                            + generationBestCost
                            + " | Average: "
                            + statistics
                                    .getAverageCost()
                            + " | Unique Routes: "
                            + statistics
                                    .getUniqueRouteCount()
                            + " | Global Best: "
                            + globalBest.getCost()
                            + " | Exploration: "
                            + QuantumPositionRouteGenerator
                                    .getExplorationRate()
            );


            System.out.println(
                    "  Best Route: "
                            + generationBestRoute
            );


            System.out.println(
                    "  Generations Without Improvement: "
                            + generationsWithoutImprovement
            );
        }
    }


    // ============================================
    // ADAPTIVE EXPLORATION
    // ============================================

    private void updateExplorationRate(
            int generation,
            int totalGenerations) {


        /*
         * Determine how long the decay should run.
         */
        int decayLimit =
                Math.min(
                        totalGenerations,
                        DECAY_GENERATIONS
                );


        double progress;


        if (decayLimit <= 1) {

            progress = 1.0;

        } else {

            progress =
                    (double) (generation - 1)
                            / (double) (decayLimit - 1);


            /*
             * Keep progress between 0 and 1.
             */
            progress =
                    Math.max(
                            0.0,
                            Math.min(
                                    1.0,
                                    progress
                            )
                    );
        }


        /*
         * Linear exploration decay.
         *
         * Example:
         *
         * Generation 1  -> 0.30
         * Generation 25 -> around 0.16
         * Generation 50 -> 0.03
         */
        double rate =
                MAX_EXPLORATION
                        -
                        (
                            (
                                MAX_EXPLORATION
                                        -
                                MIN_EXPLORATION
                            )
                            * progress
                        );


        /*
         * Increase exploration if the algorithm
         * is stuck.
         */
        if (generationsWithoutImprovement
                >= STAGNATION_THRESHOLD) {

            rate =
                    Math.min(
                            STAGNATION_EXPLORATION,
                            rate + STAGNATION_BOOST
                    );
        }


        /*
         * Safety limits.
         */
        rate =
                Math.max(
                        MIN_EXPLORATION,
                        Math.min(
                                MAX_EXPLORATION,
                                rate
                        )
                );


        /*
         * Apply new exploration rate.
         */
        QuantumPositionRouteGenerator
                .setExplorationRate(
                        rate
                );
    }


    // ============================================
    // CONVERGENCE CHECK
    // ============================================

    public boolean hasConverged(
            int patience) {

        if (patience <= 0) {

            throw new IllegalArgumentException(
                    "Patience must be greater than 0."
            );
        }


        return generationsWithoutImprovement
                >= patience;
    }


    // ============================================
    // GET BEST ROUTE
    // ============================================

    public List<Location> getBestRoute() {

        return globalBest.getRoute();
    }


    // ============================================
    // GET BEST COST
    // ============================================

    public double getBestCost() {

        return globalBest.getCost();
    }


    // ============================================
    // GET STAGNATION COUNT
    // ============================================

    public int getGenerationsWithoutImprovement() {

        return generationsWithoutImprovement;
    }


    // ============================================
    // CONVERGENCE TRACKING GETTERS
    // ============================================

    public int getFirstBestGeneration() {
        return firstBestGeneration;
    }

    public int getLastImprovementGeneration() {
        return lastImprovementGeneration;
    }


    // ============================================
    // LOCAL SEARCH GETTERS
    // ============================================

    public int getTotalRoutesEvaluated() {
        return totalRoutesEvaluated;
    }

    public int getRoutesImproved() {
        return routesImproved;
    }

    public int getTotalImprovements() {
        return totalImprovements;
    }

    public double getTotalCostImprovement() {
        return totalCostImprovement;
    }

    public double getImprovementRate() {
        if (totalRoutesEvaluated == 0) {
            return 0.0;
        }
        return ((double) routesImproved / totalRoutesEvaluated) * 100.0;
    }

    public double getAverageCostImprovement() {
        if (routesImproved == 0) {
            return 0.0;
        }
        return totalCostImprovement / routesImproved;
    }
}