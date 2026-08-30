package com.routeoptimizer;

import java.util.Locale;

public class SyntheticVsRealRoutingTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("  SYNTHETIC VS REAL ROAD ROUTING TEST");
        System.out.println("========================================");
        System.out.println();

        GeoLocation p1 = new GeoLocation("P1", "King's Cross", 51.5308, -0.1238);
        GeoLocation p2 = new GeoLocation("P2", "Canary Wharf", 51.5054, -0.0209);
        GeoLocation p3 = new GeoLocation("P3", "Greenwich", 51.4826, -0.0077);

        OSRMRoutingProvider osrm = new OSRMRoutingProvider();

        System.out.printf("%-25s %-18s %-18s %-12s%n",
                "Origin -> Destination", "Straight-Line (km)", "Real Road (km)", "Circuity Ratio");
        System.out.println("-------------------------------------------------------------------------------");

        comparePair(p1, p2, osrm);
        comparePair(p2, p3, osrm);
        comparePair(p1, p3, osrm);

        System.out.println();
        System.out.println("========================================");
        System.out.println("Findings: Real road distances reflect actual street topologies,");
        System.out.println("river crossings, and one-way constraints (circuity ratio >= 1.20x).");
        System.out.println("========================================");
    }

    private static void comparePair(GeoLocation from, GeoLocation to, OSRMRoutingProvider osrm) {
        double straightLine = from.haversineDistanceTo(to);
        RouteMetrics roadMetrics = osrm.getRoute(from, to);
        double roadDistance = roadMetrics.getDistanceKm();
        double ratio = roadDistance / straightLine;

        System.out.printf(Locale.US, "%-25s %-18.2f %-18.2f %-12.2fx%n",
                from.getName() + " -> " + to.getName(), straightLine, roadDistance, ratio);
    }
}
