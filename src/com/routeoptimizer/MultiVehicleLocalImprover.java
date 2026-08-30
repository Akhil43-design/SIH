package com.routeoptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiVehicleLocalImprover {

    public static class FleetImprovementResult {
        private final FleetRoutePlan plan;
        private final int improvementCount;

        public FleetImprovementResult(FleetRoutePlan plan, int improvementCount) {
            this.plan = plan;
            this.improvementCount = improvementCount;
        }

        public FleetRoutePlan getPlan() {
            return plan;
        }

        public int getImprovementCount() {
            return improvementCount;
        }
    }

    public static FleetImprovementResult improveFleetPlan(
            FleetRoutePlan initialPlan,
            List<Vehicle> vehicles,
            List<Customer> allCustomers,
            Location depot,
            RoadNetwork network,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction,
            int maxPasses) {

        if (initialPlan == null || vehicles == null || vehicles.isEmpty()) {
            return new FleetImprovementResult(initialPlan, 0);
        }

        // Build working map of vehicle -> customer list
        Map<Integer, List<Customer>> currentAssignment = new HashMap<>();
        for (int i = 0; i < vehicles.size(); i++) {
            currentAssignment.put(i, new ArrayList<>());
        }

        List<VehicleRoute> vRoutes = initialPlan.getVehicleRoutes();
        for (int i = 0; i < vRoutes.size() && i < vehicles.size(); i++) {
            currentAssignment.put(i, new ArrayList<>(vRoutes.get(i).getCustomers()));
        }

        FleetRoutePlan bestPlan = initialPlan;
        double bestFitness = initialPlan.getOverallFitness();
        int totalImprovements = 0;

        for (int pass = 0; pass < maxPasses; pass++) {
            boolean improvedInPass = false;

            // 1. Intra-Route 2-Opt & Swaps for each vehicle
            for (int v = 0; v < vehicles.size(); v++) {
                List<Customer> routeList = currentAssignment.get(v);
                if (routeList.size() < 2) continue;

                // 2-opt reversal
                for (int i = 0; i < routeList.size() - 1; i++) {
                    for (int j = i + 1; j < routeList.size(); j++) {
                        List<Customer> modified = new ArrayList<>(routeList);
                        reverseSubsegment(modified, i, j);

                        currentAssignment.put(v, modified);
                        FleetRoutePlan candidate = buildPlanFromAssignment(
                                currentAssignment, vehicles, allCustomers, depot, network, trafficModel, fitnessFunction
                        );

                        if (candidate.getOverallFitness() < bestFitness - 1e-6) {
                            bestFitness = candidate.getOverallFitness();
                            bestPlan = candidate;
                            improvedInPass = true;
                            totalImprovements++;
                            routeList = modified;
                        } else {
                            currentAssignment.put(v, routeList); // revert
                        }
                    }
                }

                // Intra-route swap
                for (int i = 0; i < routeList.size() - 1; i++) {
                    for (int j = i + 1; j < routeList.size(); j++) {
                        List<Customer> modified = new ArrayList<>(routeList);
                        Customer temp = modified.get(i);
                        modified.set(i, modified.get(j));
                        modified.set(j, temp);

                        currentAssignment.put(v, modified);
                        FleetRoutePlan candidate = buildPlanFromAssignment(
                                currentAssignment, vehicles, allCustomers, depot, network, trafficModel, fitnessFunction
                        );

                        if (candidate.getOverallFitness() < bestFitness - 1e-6) {
                            bestFitness = candidate.getOverallFitness();
                            bestPlan = candidate;
                            improvedInPass = true;
                            totalImprovements++;
                            routeList = modified;
                        } else {
                            currentAssignment.put(v, routeList); // revert
                        }
                    }
                }
            }

            // 2. Inter-Route Relocate (Move customer from Vehicle A to Vehicle B)
            for (int v1 = 0; v1 < vehicles.size(); v1++) {
                List<Customer> r1 = currentAssignment.get(v1);
                if (r1.isEmpty()) continue;

                for (int v2 = 0; v2 < vehicles.size(); v2++) {
                    if (v1 == v2) continue;
                    List<Customer> r2 = currentAssignment.get(v2);

                    for (int i = 0; i < r1.size(); i++) {
                        Customer cust = r1.get(i);
                        // Try inserting cust at all positions in r2
                        for (int j = 0; j <= r2.size(); j++) {
                            List<Customer> newR1 = new ArrayList<>(r1);
                            newR1.remove(i);
                            List<Customer> newR2 = new ArrayList<>(r2);
                            newR2.add(j, cust);

                            currentAssignment.put(v1, newR1);
                            currentAssignment.put(v2, newR2);

                            FleetRoutePlan candidate = buildPlanFromAssignment(
                                    currentAssignment, vehicles, allCustomers, depot, network, trafficModel, fitnessFunction
                            );

                            if (candidate.getOverallFitness() < bestFitness - 1e-6) {
                                bestFitness = candidate.getOverallFitness();
                                bestPlan = candidate;
                                improvedInPass = true;
                                totalImprovements++;
                                r1 = newR1;
                                r2 = newR2;
                                break;
                            } else {
                                currentAssignment.put(v1, r1);
                                currentAssignment.put(v2, r2);
                            }
                        }
                    }
                }
            }

            // 3. Inter-Route Customer Swap
            for (int v1 = 0; v1 < vehicles.size() - 1; v1++) {
                List<Customer> r1 = currentAssignment.get(v1);
                if (r1.isEmpty()) continue;

                for (int v2 = v1 + 1; v2 < vehicles.size(); v2++) {
                    List<Customer> r2 = currentAssignment.get(v2);
                    if (r2.isEmpty()) continue;

                    for (int i = 0; i < r1.size(); i++) {
                        for (int j = 0; j < r2.size(); j++) {
                            List<Customer> newR1 = new ArrayList<>(r1);
                            List<Customer> newR2 = new ArrayList<>(r2);

                            Customer c1 = newR1.get(i);
                            Customer c2 = newR2.get(j);
                            newR1.set(i, c2);
                            newR2.set(j, c1);

                            currentAssignment.put(v1, newR1);
                            currentAssignment.put(v2, newR2);

                            FleetRoutePlan candidate = buildPlanFromAssignment(
                                    currentAssignment, vehicles, allCustomers, depot, network, trafficModel, fitnessFunction
                            );

                            if (candidate.getOverallFitness() < bestFitness - 1e-6) {
                                bestFitness = candidate.getOverallFitness();
                                bestPlan = candidate;
                                improvedInPass = true;
                                totalImprovements++;
                                r1 = newR1;
                                r2 = newR2;
                                break;
                            } else {
                                currentAssignment.put(v1, r1);
                                currentAssignment.put(v2, r2);
                            }
                        }
                    }
                }
            }

            if (!improvedInPass) {
                break;
            }
        }

        return new FleetImprovementResult(bestPlan, totalImprovements);
    }

    private static void reverseSubsegment(List<Customer> list, int start, int end) {
        while (start < end) {
            Customer temp = list.get(start);
            list.set(start, list.get(end));
            list.set(end, temp);
            start++;
            end--;
        }
    }

    public static FleetRoutePlan buildPlanFromAssignment(
            Map<Integer, List<Customer>> assignment,
            List<Vehicle> vehicles,
            List<Customer> allCustomers,
            Location depot,
            RoadNetwork network,
            TrafficModel trafficModel,
            FleetFitnessFunction fitnessFunction) {

        List<VehicleRoute> vRoutes = new ArrayList<>(vehicles.size());
        for (int v = 0; v < vehicles.size(); v++) {
            Vehicle vehicle = vehicles.get(v);
            Location vehicleDepot = (vehicle.getCurrentLocation() != null) ? vehicle.getCurrentLocation() : depot;
            List<Customer> cList = assignment.getOrDefault(v, new ArrayList<>());
            vRoutes.add(new VehicleRoute(vehicle, cList, vehicleDepot, network, trafficModel));
        }

        return new FleetRoutePlan(depot, vRoutes, allCustomers, fitnessFunction);
    }
}
