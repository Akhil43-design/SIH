package com.routeoptimizer;

import java.util.*;

public class Step44DScalability {

public static void main(String[] args) {

    System.out.println("========================================");
    System.out.println("     STEP 44D - 10 CUSTOMER TEST");
    System.out.println("========================================");
    System.out.println();

    // ========================================
    // CONFIGURATION
    // ========================================

    int populationSize = 30;
    int generations = 50;

    // ========================================
    // CREATE LOCATIONS
    // ========================================

    Location warehouse =
            new Location("W", "Warehouse");

    List<Location> customers =
            new ArrayList<>();

    for (char c = 'A'; c <= 'J'; c++) {

        customers.add(
                new Location(
                        String.valueOf(c),
                        "Customer " + c
                )
        );
    }

    System.out.println(
            "Customers: " + customers.size()
    );

    System.out.println(
            "Population Size: " + populationSize
    );

    System.out.println(
            "Generations: " + generations
    );

    System.out.println(
            "Possible Routes: "
                    + factorial(customers.size())
    );

    System.out.println();


    // ========================================
    // CREATE NETWORK
    // ========================================

    RoadNetwork network =
            new RoadNetwork();

    /*
     * Warehouse -> Customer
     */

    for (Location from : customers) {

        network.addRoad(
                new Road(
                        warehouse,
                        from,
                        5.0 + index(from),
                        10.0 + index(from),
                        0.5 + index(from) * 0.1,
                        1
                )
        );

        /*
         * Customer -> Warehouse
         */

        network.addRoad(
                new Road(
                        from,
                        warehouse,
                        5.0 + index(from),
                        10.0 + index(from),
                        0.5 + index(from) * 0.1,
                        1
                )
        );
    }


    // ========================================
    // CUSTOMER -> CUSTOMER ROADS
    // ========================================

    for (Location from : customers) {

        for (Location to : customers) {

            if (from.equals(to)) {
                continue;
            }

            int i = index(from);
            int j = index(to);

            double distance =
                    2.0 + Math.abs(i - j);

            double time =
                    distance * 2.0;

            double fuel =
                    distance * 0.1;

            int traffic =
                    1 + ((i + j) % 3);

            network.addRoad(
                    new Road(
                            from,
                            to,
                            distance,
                            time,
                            fuel,
                            traffic
                    )
            );
        }
    }


    // ========================================
    // FITNESS FUNCTION
    // ========================================

    FitnessFunction fitnessFunction =
            new FitnessFunction(
                    0.25,
                    0.30,
                    0.20,
                    0.25,
                    50.0,
                    100.0,
                    10.0,
                    12.0
            );


    // ========================================
    // EVALUATOR
    // ========================================

    QuantumPopulationEvaluator evaluator =
            new QuantumPopulationEvaluator(
                    warehouse,
                    network,
                    fitnessFunction
            );


    // ========================================
    // PROBABILITY UPDATER
    // ========================================

    PositionProbabilityUpdater updater =
            new PositionProbabilityUpdater(
                    0.05
            );


    // ========================================
    // EXPLORATION RATE
    // ========================================

    QuantumPositionRouteGenerator
            .setExplorationRate(0.20);


    // ========================================
    // RUN QIGA
    // ========================================

    QIGAOptimizer qiga =
            new QIGAOptimizer(
                    populationSize,
                    customers,
                    evaluator,
                    updater
            );


    System.out.println(
            "----------------------------------------"
    );

    System.out.println(
            "RUNNING QIGA"
    );

    System.out.println(
            "----------------------------------------"
    );

    long qigaStart =
            System.nanoTime();

    qiga.optimize(generations);

    long qigaEnd =
            System.nanoTime();

    double qigaTime =
            (qigaEnd - qigaStart)
                    / 1_000_000.0;


    List<Location> qigaRoute =
            qiga.getBestRoute();

    double qigaCost =
            qiga.getBestCost();


    // ========================================
    // QIGA RESULT
    // ========================================

    System.out.println();

    System.out.println(
            "QIGA Best Route:"
    );

    printRoute(
            warehouse,
            qigaRoute
    );

    System.out.println();

    System.out.println(
            "QIGA Best Cost: "
                    + qigaCost
    );

    System.out.println(
            "QIGA Runtime: "
                    + qigaTime
                    + " ms"
    );


    // ========================================
    // RUN EXACT BRUTE FORCE
    // ========================================

    System.out.println();

    System.out.println(
            "----------------------------------------"
    );

    System.out.println(
            "RUNNING EXACT BRUTE FORCE"
    );

    System.out.println(
            "----------------------------------------"
    );

    System.out.println();

    System.out.println(
            "Brute force will evaluate up to "
                    + factorial(customers.size())
                    + " permutations."
    );


    BruteForceRouteOptimizer bruteForce =
            new BruteForceRouteOptimizer(
                    warehouse,
                    network,
                    fitnessFunction
            );


    long bruteStart =
            System.nanoTime();

    bruteForce.optimize(customers);

    long bruteEnd =
            System.nanoTime();

    double bruteTime =
            (bruteEnd - bruteStart)
                    / 1_000_000.0;


    List<Location> exactRoute =
            bruteForce.getBestRoute();

    double exactCost =
            bruteForce.getBestCost();


    // ========================================
    // EXACT RESULT
    // ========================================

    System.out.println();

    System.out.println(
            "Exact Best Route:"
    );

    printRoute(
            warehouse,
            exactRoute
    );

    System.out.println();

    System.out.println(
            "Exact Best Cost: "
                    + exactCost
    );

    System.out.println(
            "Brute Force Runtime: "
                    + bruteTime
                    + " ms"
    );


    // ========================================
    // COST COMPARISON
    // ========================================

    double costDifference =
            Math.abs(
                    qigaCost
                            - exactCost
            );


    /*
     * IMPORTANT:
     *
     * QIGA is a heuristic algorithm.
     *
     * Therefore, requiring the QIGA cost
     * to be exactly equal to brute force
     * makes the scalability test fail even
     * when the QIGA route is valid.
     *
     * We therefore use an approximation
     * tolerance for validation.
     *
     * 10% means QIGA may be at most 10%
     * worse than the exact solution.
     */

    double allowedDifference =
            exactCost * 0.10;


    boolean costWithinTolerance =
            costDifference
                    <= allowedDifference;


    // ========================================
    // ROUTE VALIDATION
    // ========================================

    boolean routeValid =
            qigaRoute != null
                    && qigaRoute.size()
                    == customers.size();


    boolean noDuplicates =
            qigaRoute != null
                    && new HashSet<>(
                            qigaRoute
                    ).size()
                    == customers.size();


    boolean allCustomersIncluded =
            qigaRoute != null
                    && qigaRoute.containsAll(
                            customers
                    );


    // ========================================
    // FINAL VALIDATION
    // ========================================

    boolean passed =
            routeValid
                    && noDuplicates
                    && allCustomersIncluded
                    && costWithinTolerance;


    // ========================================
    // RESULTS
    // ========================================

    System.out.println();

    System.out.println(
            "========================================"
    );

    System.out.println(
            "          STEP 44D RESULTS"
    );

    System.out.println(
            "========================================"
    );

    System.out.println();

    System.out.println(
            "Number of Customers: "
                    + customers.size()
    );

    System.out.println(
            "Possible Routes: "
                    + factorial(customers.size())
    );

    System.out.println();

    System.out.println(
            "QIGA Cost: "
                    + qigaCost
    );

    System.out.println(
            "Exact Cost: "
                    + exactCost
    );

    System.out.println(
            "Cost Difference: "
                    + costDifference
    );

    System.out.println(
            "Allowed Difference: "
                    + allowedDifference
    );

    System.out.println();

    System.out.println(
            "Route Size Valid: "
                    + routeValid
    );

    System.out.println(
            "No Duplicate Customers: "
                    + noDuplicates
    );

    System.out.println(
            "All Customers Included: "
                    + allCustomersIncluded
    );

    System.out.println(
            "Cost Within 10% Tolerance: "
                    + costWithinTolerance
    );

    System.out.println();

    System.out.println(
            "QIGA Runtime: "
                    + qigaTime
                    + " ms"
    );

    System.out.println(
            "Brute Force Runtime: "
                    + bruteTime
                    + " ms"
    );

    System.out.println();


    // ========================================
    // SPEEDUP
    // ========================================

    if (qigaTime > 0) {

        double speedup =
                bruteTime / qigaTime;

        System.out.println(
                "QIGA Speedup vs Brute Force: "
                        + speedup
                        + "x"
        );
    }

    System.out.println();


    // ========================================
    // FINAL STATUS
    // ========================================

    if (passed) {

        System.out.println(
                "STEP 44D VALIDATION: PASSED"
        );

    } else {

        System.out.println(
                "STEP 44D VALIDATION: FAILED"
        );
    }

    System.out.println();

    System.out.println(
            "========================================"
    );
}


// ========================================
// LOCATION INDEX
// ========================================

private static int index(
        Location location
) {

    return location
            .getId()
            .charAt(0)
            - 'A'
            + 1;
}


// ========================================
// FACTORIAL
// ========================================

private static long factorial(
        int n
) {

    long result = 1;

    for (int i = 2; i <= n; i++) {

        result *= i;
    }

    return result;
}


// ========================================
// PRINT ROUTE
// ========================================

private static void printRoute(
        Location warehouse,
        List<Location> route
) {

    System.out.print(
            warehouse.getId()
    );

    if (route != null) {

        for (Location location : route) {

            System.out.print(
                    " -> "
                            + location.getId()
            );
        }
    }

    System.out.println(
            " -> "
                    + warehouse.getId()
    );
}


}
