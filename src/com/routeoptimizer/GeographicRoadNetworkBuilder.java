package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class GeographicRoadNetworkBuilder {

    private final RoutingProvider routingProvider;

    public GeographicRoadNetworkBuilder(RoutingProvider routingProvider) {
        this.routingProvider = routingProvider != null ? routingProvider : new OSRMRoutingProvider();
    }

    public GeographicRoadNetworkBuilder() {
        this(new OSRMRoutingProvider());
    }

    public RoadNetwork buildRoadNetwork(List<? extends Location> locations) {
        if (locations == null || locations.size() < 2) {
            throw new IllegalArgumentException("Must provide at least 2 locations to build a network.");
        }

        RoadNetwork network = new RoadNetwork();
        List<GeoLocation> geoLocations = new ArrayList<>(locations.size());

        for (Location loc : locations) {
            if (loc instanceof GeoCustomer) {
                GeoCustomer gc = (GeoCustomer) loc;
                geoLocations.add(gc.toGeoLocation());
            } else if (loc instanceof GeoLocation) {
                geoLocations.add((GeoLocation) loc);
            } else {
                // If it's a plain Location, assign default coordinate space or fallback
                geoLocations.add(new GeoLocation(loc.getId(), loc.getName(), 37.7749, -122.4194));
            }
        }

        for (int i = 0; i < geoLocations.size(); i++) {
            for (int j = 0; j < geoLocations.size(); j++) {
                if (i == j) continue;

                GeoLocation from = geoLocations.get(i);
                GeoLocation to = geoLocations.get(j);
                Location origFrom = locations.get(i);
                Location origTo = locations.get(j);

                RouteMetrics metrics = routingProvider.getRoute(from, to);

                double distanceKm = metrics.getDistanceKm();
                double travelTimeMin = metrics.getTravelTimeMinutes();
                double fuelLitres = distanceKm * 0.10; // 10 L per 100 km average
                int trafficLevel = 1 + ((i + j) % 3);

                Road road = new Road(origFrom, origTo, distanceKm, travelTimeMin, fuelLitres, trafficLevel);
                network.addRoad(road);
            }
        }

        return network;
    }

    public RoutingProvider getRoutingProvider() {
        return routingProvider;
    }
}
