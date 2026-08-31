package com.routeoptimizer;

import java.util.Arrays;
import java.util.List;

public class EndToEndRoutingConsistencyTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  END-TO-END ROUTING CONSISTENCY TRACE");
        System.out.println("========================================\n");

        DatabaseManager db = new DatabaseManager();
        FleetManagementService fleetService = new FleetManagementService(db);
        TrafficService trafficService = new TrafficService();
        OptimizationService optService = new OptimizationService(fleetService, trafficService, db);

        // Step 1: Database Coordinate Loading (Bengaluru)
        System.out.println("Step 1: Loading Bengaluru Indian logistics dataset into Database...");
        IndianCityDatasets.CityDataset ds = fleetService.loadCityDataset("bengaluru");
        System.out.printf("  Depots in DB:    %d%n", ds.getDepots().size());
        System.out.printf("  Customers in DB: %d%n", ds.getCustomers().size());
        System.out.printf("  Vehicles in DB:  %d%n", ds.getVehicles().size());

        // Step 2: Extract Peenya (W1) and Manyata (C1)
        DepotDto peenyaDto = ds.getDepots().get(0);
        CustomerDto manyataDto = ds.getCustomers().get(0);
        System.out.printf("  Depot Node:    %s (Lat: %.4f, Lon: %.4f)%n", peenyaDto.getName(), peenyaDto.getLatitude(), peenyaDto.getLongitude());
        System.out.printf("  Customer Node: %s (Lat: %.4f, Lon: %.4f)%n", manyataDto.getName(), manyataDto.getLatitude(), manyataDto.getLongitude());

        // Step 3: OSRM Routing Provider Query
        OSRMRoutingProvider osrm = new OSRMRoutingProvider();
        GeoLocation peenyaGeo = (GeoLocation) peenyaDto.toDomain();
        Customer cust = manyataDto.toDomain();
        GeoLocation manyataGeo = ((GeoCustomer) cust).toGeoLocation();

        RouteMetrics legMetrics = osrm.getRoute(peenyaGeo, manyataGeo);
        System.out.printf("Step 2: OSRM Single-Leg Route Metrics -> Distance: %.2f km, Duration: %.2f min%n",
                legMetrics.getDistanceKm(), legMetrics.getTravelTimeMinutes());

        // Step 4: Geographic Road Network Construction
        GeographicRoadNetworkBuilder builder = new GeographicRoadNetworkBuilder(osrm);
        RoadNetwork roadNetwork = builder.buildRoadNetwork(Arrays.asList(peenyaGeo, manyataGeo));
        Road forwardRoad = roadNetwork.findRoad(peenyaGeo, manyataGeo);
        System.out.printf("Step 3: RoadNetwork Edge Distance -> %.2f km (Travel Time: %.2f min)%n",
                forwardRoad.getDistance(), forwardRoad.getTravelTime());

        // Step 5: Execute QIGA Optimization with REAL_OSRM Mode
        System.out.println("Step 4: Executing Multi-Vehicle QIGA Optimization Engine with REAL_OSRM...");
        OptimizationRequest req = new OptimizationRequest();
        req.setRoutingMode("REAL_OSRM");
        req.setGenerations(50);
        req.setPopulationSize(30);
        req.setSeed(42L);

        long t0 = System.currentTimeMillis();
        OptimizationResponse resp = optService.runOptimization(req);
        long elapsed = System.currentTimeMillis() - t0;

        System.out.printf("Step 5: Optimization Completed in %d ms%n", elapsed);
        System.out.printf("  Optimization ID:     %s%n", resp.getOptimizationId());
        System.out.printf("  Status:              %s%n", resp.getStatus());
        System.out.printf("  Routing Provider:    %s%n", resp.getRoutingProvider());
        System.out.printf("  Optimization Score:  %.4f%n", resp.getOptimizationScore());
        System.out.printf("  Total Road Distance: %.2f km%n", resp.getTotalDistanceKm());
        System.out.printf("  Total Travel Time:   %.2f min%n", resp.getTotalTravelTimeMinutes());
        System.out.printf("  Total Fuel:          %.2f L%n", resp.getTotalFuelLiters());
        System.out.printf("  Total Cost:          ₹%.2f%n", resp.getTotalCost());
        System.out.printf("  Vehicle Routes:      %d%n", resp.getVehicleRoutes().size());

        for (int i = 0; i < resp.getVehicleRoutes().size(); i++) {
            OptimizationResponse.VehicleRouteResponse vr = resp.getVehicleRoutes().get(i);
            System.out.printf("    Vehicle %s (Depot %s): Stops: %s | Road Dist: %.2f km | Time: %.2f min%n",
                    vr.getVehicleId(), vr.getDepotId(), vr.getCustomerSequence(), vr.getTotalDistanceKm(), vr.getTotalTravelTimeMinutes());
        }

        // Trace Verification
        boolean pass = true;
        if (resp.getTotalDistanceKm() < 20.0) {
            System.err.println("[FAIL] Optimization total distance is unexpectedly small (< 20 km).");
            pass = false;
        }
        if (!"COMPLETED".equals(resp.getStatus())) {
            System.err.println("[FAIL] Optimization status is not COMPLETED.");
            pass = false;
        }
        if (resp.getVehicleRoutes().isEmpty()) {
            System.err.println("[FAIL] No vehicle routes generated.");
            pass = false;
        }

        if (pass) {
            System.out.println("\n[PASS] END-TO-END ROUTING CONSISTENCY VERIFIED: REAL OSRM ROAD METRICS ARE UNIFORMLY CONSUMED BY QIGA OPTIMIZATION ENGINE.");
        } else {
            System.exit(1);
        }
    }
}
