package com.routeoptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class FleetQuantumIndividual {

    private final int customerCount;
    private final int vehicleCount;
    private final List<PositionQBit> positionQBits;
    private final List<VehicleAssignmentQBit> assignmentQBits;

    public FleetQuantumIndividual(int customerCount, int vehicleCount) {
        this.customerCount = customerCount;
        this.vehicleCount = vehicleCount;

        this.positionQBits = new ArrayList<>(customerCount);
        for (int i = 0; i < customerCount; i++) {
            this.positionQBits.add(new PositionQBit(customerCount));
        }

        this.assignmentQBits = new ArrayList<>(customerCount);
        for (int i = 0; i < customerCount; i++) {
            this.assignmentQBits.add(new VehicleAssignmentQBit(vehicleCount));
        }
    }

    public List<Customer> generateCustomerPermutation(List<Customer> customers, Random random, double explorationRate) {
        List<Customer> remaining = new ArrayList<>(customers);
        List<Customer> route = new ArrayList<>(customerCount);

        for (int pos = 0; pos < customerCount; pos++) {
            if (remaining.size() == 1) {
                route.add(remaining.remove(0));
                break;
            }

            int selectedIndex;
            if (random.nextDouble() < explorationRate) {
                selectedIndex = random.nextInt(remaining.size());
            } else {
                PositionQBit qBit = positionQBits.get(pos);
                selectedIndex = sampleFromRemaining(qBit, customers, remaining, random);
            }
            route.add(remaining.remove(selectedIndex));
        }

        return route;
    }

    private int sampleFromRemaining(
            PositionQBit qBit,
            List<Customer> allCustomers,
            List<Customer> remaining,
            Random random) {

        double[] fullProbs = qBit.getProbabilities();
        double[] subProbs = new double[remaining.size()];
        double sum = 0.0;

        for (int i = 0; i < remaining.size(); i++) {
            Customer cust = remaining.get(i);
            int origIdx = allCustomers.indexOf(cust);
            double p = (origIdx >= 0 && origIdx < fullProbs.length) ? fullProbs[origIdx] : 1.0;
            subProbs[i] = p;
            sum += p;
        }

        if (sum <= 0) {
            return random.nextInt(remaining.size());
        }

        double r = random.nextDouble() * sum;
        double cumulative = 0.0;
        for (int i = 0; i < subProbs.length; i++) {
            cumulative += subProbs[i];
            if (r <= cumulative) {
                return i;
            }
        }
        return remaining.size() - 1;
    }

    public Map<Integer, List<Customer>> generateFleetAssignment(
            List<Customer> orderedCustomers,
            List<Customer> allCustomers,
            Random random,
            double explorationRate) {

        Map<Integer, List<Customer>> vehicleMap = new HashMap<>();
        for (int v = 0; v < vehicleCount; v++) {
            vehicleMap.put(v, new ArrayList<>());
        }

        for (Customer c : orderedCustomers) {
            int custIdx = allCustomers.indexOf(c);
            int vehicleIdx;
            if (custIdx >= 0 && custIdx < assignmentQBits.size()) {
                vehicleIdx = assignmentQBits.get(custIdx).measure(random, explorationRate);
            } else {
                vehicleIdx = random.nextInt(vehicleCount);
            }
            vehicleMap.get(vehicleIdx).add(c);
        }

        return vehicleMap;
    }

    public void update(
            List<Customer> bestOrderedPermutation,
            Map<Customer, Integer> bestCustomerVehicleMap,
            List<Customer> allCustomers,
            double learningRate) {

        // Update Position Q-Bits based on customer order
        for (int pos = 0; pos < bestOrderedPermutation.size() && pos < positionQBits.size(); pos++) {
            Customer c = bestOrderedPermutation.get(pos);
            int targetIndex = allCustomers.indexOf(c);
            if (targetIndex >= 0) {
                PositionQBit qbit = positionQBits.get(pos);
                double[] probs = qbit.getProbabilities();
                double total = 0.0;

                for (int cust = 0; cust < probs.length; cust++) {
                    double current = probs[cust];
                    if (cust == targetIndex) {
                        current = current + learningRate * (1.0 - current);
                    } else {
                        current = current * (1.0 - learningRate);
                    }
                    if (current < 0.001) current = 0.001;
                    probs[cust] = current;
                    total += current;
                }

                for (int cust = 0; cust < probs.length; cust++) {
                    qbit.setProbability(cust, probs[cust] / total);
                }
            }
        }

        // Update Vehicle Assignment Q-Bits
        for (int i = 0; i < allCustomers.size() && i < assignmentQBits.size(); i++) {
            Customer c = allCustomers.get(i);
            Integer targetVehicle = bestCustomerVehicleMap.get(c);
            if (targetVehicle != null) {
                assignmentQBits.get(i).update(targetVehicle, learningRate);
            }
        }
    }

    public List<PositionQBit> getPositionQBits() {
        return positionQBits;
    }

    public List<VehicleAssignmentQBit> getAssignmentQBits() {
        return assignmentQBits;
    }
}
