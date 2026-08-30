package com.routeoptimizer;

public interface RoutingProvider {

    RouteMetrics getRoute(GeoLocation origin, GeoLocation destination);

    double getDistance(GeoLocation origin, GeoLocation destination);

    double getTravelTime(GeoLocation origin, GeoLocation destination);

    String getProviderName();

    boolean isAvailable();
}
