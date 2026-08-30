package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimeDependentTrafficTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("   TIME-DEPENDENT TRAFFIC TEST");
        System.out.println("========================================");
        System.out.println();

        Location depot = new Location("W", "Depot");
        Location c1 = new Location("C1", "Customer 1");
        Location c2 = new Location("C2", "Customer 2");

        Road r1 = new Road(depot, c1, 10.0, 20.0, 1.0, 2);
        Road r2 = new Road(c1, c2, 15.0, 30.0, 1.5, 3);
        Road r3 = new Road(c2, depot, 12.0, 24.0, 1.2, 1);

        TimeDependentTrafficModel traffic = new TimeDependentTrafficModel();

        // 1. Test same road at different times (04:00 night vs 08:30 morning rush)
        double offPeakTime = traffic.calculateAdjustedTravelTime(r1, 240.0); // 04:00
        double peakTime = traffic.calculateAdjustedTravelTime(r1, 510.0);    // 08:30

        System.out.printf("Road W->C1 (Base Time: %.1f min):%n", r1.getTravelTime());
        System.out.printf("  04:00 (Off-Peak): %.2f min (Multiplier: %.2fx)%n",
                offPeakTime, offPeakTime / r1.getTravelTime());
        System.out.printf("  08:30 (Morning Peak): %.2f min (Multiplier: %.2fx)%n",
                peakTime, peakTime / r1.getTravelTime());

        boolean test1 = peakTime > offPeakTime;
        System.out.println("Test 1 (Peak > Off-Peak): " + (test1 ? "PASSED" : "FAILED"));
        System.out.println();

        // 2. Test timeline edge departure time progression
        Customer cust1 = new Customer("C1", "Customer 1", 10.0, DeliveryPriority.HIGH, 10.0, 0.0, 200.0);
        Customer cust2 = new Customer("C2", "Customer 2", 10.0, DeliveryPriority.HIGH, 10.0, 0.0, 200.0);
        Vehicle vehicle = new Vehicle("V1", 100.0, depot, 0.12, 10.0);

        RoadNetwork network = new RoadNetwork();
        network.addRoad(r1);
        network.addRoad(r2);
        network.addRoad(r3);

        VehicleRoute route = new VehicleRoute(vehicle, List.of(cust1, cust2), depot, network, traffic);
        System.out.println("Dynamic Vehicle Route Timeline:");
        System.out.printf("  Total Distance: %.2f km%n", route.getTotalDistance());
        System.out.printf("  Total Travel Time (Time-Dependent): %.2f min%n", route.getTotalTravelTime());
        System.out.printf("  Total Service Time: %.2f min%n", route.getTotalServiceTime());
        System.out.printf("  Total Waiting Time: %.2f min%n", route.getTotalWaitingTime());

        boolean test2 = route.getTotalTravelTime() > 0;
        System.out.println("Test 2 (Dynamic Timeline Execution): " + (test2 ? "PASSED" : "FAILED"));

        System.out.println();
        System.out.println("========================================");
        System.out.println("TIME-DEPENDENT TRAFFIC: " + (test1 && test2 ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
