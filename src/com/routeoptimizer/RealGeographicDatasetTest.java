package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RealGeographicDatasetTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("    REAL GEOGRAPHIC DATASET TEST");
        System.out.println("========================================");
        System.out.println();

        // Real Geographic Coordinates in London Logistics Area
        // Depots:
        GeoLocation depotNorth = new GeoLocation("D1", "North London Depot (King's Cross)", 51.5308, -0.1238);
        GeoLocation depotSouth = new GeoLocation("D2", "South London Depot (London Bridge)", 51.5055, -0.0863);

        // 8 Real Demonstration Customer Points:
        List<GeoCustomer> customers = new ArrayList<>();
        customers.add(new GeoCustomer("C1", "Westminster", 51.4995, -0.1332, 20.0, DeliveryPriority.HIGH, 5.0, 10.0, 60.0));
        customers.add(new GeoCustomer("C2", "Covent Garden", 51.5117, -0.1240, 15.0, DeliveryPriority.MEDIUM, 4.0, 15.0, 80.0));
        customers.add(new GeoCustomer("C3", "Canary Wharf", 51.5054, -0.0209, 30.0, DeliveryPriority.HIGH, 6.0, 20.0, 90.0));
        customers.add(new GeoCustomer("C4", "Shoreditch", 51.5245, -0.0774, 25.0, DeliveryPriority.MEDIUM, 5.0, 10.0, 75.0));
        customers.add(new GeoCustomer("C5", "Camden Town", 51.5390, -0.1426, 20.0, DeliveryPriority.LOW, 4.0, 30.0, 120.0));
        customers.add(new GeoCustomer("C6", "Southwark", 51.5033, -0.1017, 15.0, DeliveryPriority.HIGH, 5.0, 15.0, 70.0));
        customers.add(new GeoCustomer("C7", "Paddington", 51.5154, -0.1755, 35.0, DeliveryPriority.MEDIUM, 7.0, 25.0, 110.0));
        customers.add(new GeoCustomer("C8", "Greenwich", 51.4826, -0.0077, 20.0, DeliveryPriority.LOW, 5.0, 35.0, 130.0));

        System.out.println("Depots (2):");
        System.out.println("  " + depotNorth);
        System.out.println("  " + depotSouth);
        System.out.println();
        System.out.println("Customers (8):");
        for (GeoCustomer c : customers) {
            System.out.println("  " + c);
        }
        System.out.println();

        // Build Road Network using GeographicRoadNetworkBuilder
        List<Location> allNodes = new ArrayList<>();
        allNodes.add(depotNorth);
        allNodes.add(depotSouth);
        allNodes.addAll(customers);

        OSRMRoutingProvider routingProvider = new OSRMRoutingProvider();
        GeographicRoadNetworkBuilder networkBuilder = new GeographicRoadNetworkBuilder(routingProvider);
        RoadNetwork network = networkBuilder.buildRoadNetwork(allNodes);

        System.out.println("Generated Road Network Edges: " + network.getRoads().size());
        int expectedEdges = allNodes.size() * (allNodes.size() - 1);
        boolean edgeCountValid = (network.getRoads().size() == expectedEdges);
        System.out.println("Full Directed Graph Completeness: " + (edgeCountValid ? "PASSED" : "FAILED"));

        // Spot check road distances
        Road sampleRoad = network.findRoad(depotNorth, customers.get(0));
        if (sampleRoad != null) {
            System.out.printf("Sample Edge [%s -> %s]: Distance = %.2f km, Time = %.2f min%n",
                    sampleRoad.getFrom().getName(), sampleRoad.getTo().getName(),
                    sampleRoad.getDistance(), sampleRoad.getTravelTime());
        }

        System.out.println();
        System.out.println("========================================");
        System.out.println("REAL GEOGRAPHIC DATASET TEST: " + (edgeCountValid ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
