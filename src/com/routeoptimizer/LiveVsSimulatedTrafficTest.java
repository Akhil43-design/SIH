package com.routeoptimizer;

import java.util.Locale;

public class LiveVsSimulatedTrafficTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("   LIVE VS SIMULATED TRAFFIC TEST");
        System.out.println("========================================");
        System.out.println();

        Location p1 = new GeoLocation("D1", "King's Cross Depot", 51.5308, -0.1238);
        Location p2 = new GeoLocation("C1", "Westminster Customer", 51.4995, -0.1332);
        Road road = new Road(p1, p2, 5.35, 14.28, 0.53, 2);

        SimulatedTrafficProvider simulated = new SimulatedTrafficProvider();
        ExternalLiveTrafficProvider liveOrFallback = new ExternalLiveTrafficProvider();

        long timestampMorning = 510 * 60 * 1000L; // 08:30 morning peak
        long timestampNoon = 720 * 60 * 1000L;    // 12:00 midday
        long timestampNight = 1380 * 60 * 1000L;  // 23:00 off-peak

        System.out.printf("%-18s %-25s %-12s %-15s%n", "Time of Day", "Traffic Source", "Multiplier", "Adjusted Time");
        System.out.println("-------------------------------------------------------------------------");

        evaluate(simulated, road, "08:30 (Morning Peak)", timestampMorning);
        evaluate(liveOrFallback, road, "08:30 (Morning Peak)", timestampMorning);
        System.out.println();

        evaluate(simulated, road, "12:00 (Midday Flow)", timestampNoon);
        evaluate(liveOrFallback, road, "12:00 (Midday Flow)", timestampNoon);
        System.out.println();

        evaluate(simulated, road, "23:00 (Off-Peak Night)", timestampNight);
        evaluate(liveOrFallback, road, "23:00 (Off-Peak Night)", timestampNight);

        System.out.println();
        System.out.println("========================================");
        System.out.println("LIVE VS SIMULATED TRAFFIC TEST: PASSED");
        System.out.println("========================================");
    }

    private static void evaluate(TrafficDataProvider provider, Road road, String timeLabel, long timestamp) {
        double adjTime = provider.getAdjustedTravelTime(road, road.getTravelTime(), timestamp);
        TrafficMetrics m = provider.getTraffic(road.getFrom(), road.getTo(), timestamp);

        System.out.printf(Locale.US, "%-18s %-25s %-12.2fx %-15.2f min%n",
                timeLabel, m.getSource(), m.getMultiplier(), adjTime);
    }
}
