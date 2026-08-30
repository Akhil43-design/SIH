package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalRouteImprover {

    public static class ImprovementResult {

        private final List<Location> route;
        private final double cost;
        private final int improvementCount;

        public ImprovementResult(
                List<Location> route,
                double cost,
                int improvementCount) {

            this.route = route;
            this.cost = cost;
            this.improvementCount = improvementCount;
        }

        public List<Location> getRoute() {
            return route;
        }

        public double getCost() {
            return cost;
        }

        public int getImprovementCount() {
            return improvementCount;
        }
    }

    public static List<Location> improve(
            List<Location> route,
            Location warehouse,
            RoadNetwork network,
            FitnessFunction fitnessFunction) {

        ImprovementResult result =
                improveWithDetails(
                        route,
                        warehouse,
                        network,
                        fitnessFunction
                );

        return result.getRoute();
    }

    public static ImprovementResult improveWithDetails(
            List<Location> route,
            Location warehouse,
            RoadNetwork network,
            FitnessFunction fitnessFunction) {

        if (route == null || route.size() <= 2) {
            Route initialRoute =
                    RouteBuilder.buildRoute(
                            warehouse,
                            route,
                            network
                    );

            double initialCost =
                    fitnessFunction.calculateCost(
                            initialRoute
                    );

            return new ImprovementResult(
                    new ArrayList<>(route),
                    initialCost,
                    0
            );
        }

        List<Location> currentOrder =
                new ArrayList<>(route);

        Route currentRoute =
                RouteBuilder.buildRoute(
                        warehouse,
                        currentOrder,
                        network
                );

        double currentCost =
                fitnessFunction.calculateCost(
                        currentRoute
                );

        int totalImprovements = 0;
        boolean improved = true;
        int maxPasses = 5;
        int pass = 0;

        while (improved && pass < maxPasses) {

            improved = false;
            pass++;

            int n = currentOrder.size();

            // 1. 2-Opt (Subsequence Reversal)
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {

                    List<Location> candidate =
                            reverseSegment(currentOrder, i, j);

                    Route candidateRoute =
                            RouteBuilder.buildRoute(
                                    warehouse,
                                    candidate,
                                    network
                            );

                    double candidateCost =
                            fitnessFunction.calculateCost(
                                    candidateRoute
                            );

                    if (candidateCost < currentCost - 1e-9) {
                        currentOrder = candidate;
                        currentCost = candidateCost;
                        totalImprovements++;
                        improved = true;
                        break;
                    }
                }
                if (improved) {
                    break;
                }
            }

            if (improved) {
                continue;
            }

            // 2. Relocate / Insert Operator
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) {
                        continue;
                    }

                    List<Location> candidate =
                            relocateCustomer(currentOrder, i, j);

                    Route candidateRoute =
                            RouteBuilder.buildRoute(
                                    warehouse,
                                    candidate,
                                    network
                            );

                    double candidateCost =
                            fitnessFunction.calculateCost(
                                    candidateRoute
                            );

                    if (candidateCost < currentCost - 1e-9) {
                        currentOrder = candidate;
                        currentCost = candidateCost;
                        totalImprovements++;
                        improved = true;
                        break;
                    }
                }
                if (improved) {
                    break;
                }
            }

            if (improved) {
                continue;
            }

            // 3. Swap Operator
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {

                    List<Location> candidate =
                            swapCustomers(currentOrder, i, j);

                    Route candidateRoute =
                            RouteBuilder.buildRoute(
                                    warehouse,
                                    candidate,
                                    network
                            );

                    double candidateCost =
                            fitnessFunction.calculateCost(
                                    candidateRoute
                            );

                    if (candidateCost < currentCost - 1e-9) {
                        currentOrder = candidate;
                        currentCost = candidateCost;
                        totalImprovements++;
                        improved = true;
                        break;
                    }
                }
                if (improved) {
                    break;
                }
            }
        }

        return new ImprovementResult(
                currentOrder,
                currentCost,
                totalImprovements
        );
    }

    private static List<Location> reverseSegment(
            List<Location> order,
            int i,
            int j) {

        List<Location> copy = new ArrayList<>(order);
        while (i < j) {
            Location temp = copy.get(i);
            copy.set(i, copy.get(j));
            copy.set(j, temp);
            i++;
            j--;
        }
        return copy;
    }

    private static List<Location> relocateCustomer(
            List<Location> order,
            int from,
            int to) {

        List<Location> copy = new ArrayList<>(order);
        Location loc = copy.remove(from);
        copy.add(to, loc);
        return copy;
    }

    private static List<Location> swapCustomers(
            List<Location> order,
            int i,
            int j) {

        List<Location> copy = new ArrayList<>(order);
        Collections.swap(copy, i, j);
        return copy;
    }
}
