package com.routeoptimizer;

public class TrafficController {

    private final TrafficService trafficService;
    private final FleetManagementService fleetService;

    public TrafficController(TrafficService trafficService, FleetManagementService fleetService) {
        this.trafficService = trafficService != null ? trafficService : new TrafficService();
        this.fleetService = fleetService != null ? fleetService : new FleetManagementService();
    }

    public TrafficUpdate updateTraffic(TrafficUpdateRequest req) {
        if (req == null) {
            throw new ValidationException("Traffic update body must not be null.");
        }
        req.validate();

        Location origin = findLocation(req.getOriginId());
        Location destination = findLocation(req.getDestinationId());

        if (origin == null || destination == null) {
            throw new ValidationException("Origin or Destination location ID not found in system.");
        }

        return trafficService.processUpdate(req, origin, destination);
    }

    private Location findLocation(String id) {
        try {
            return fleetService.getDepot(id);
        } catch (Exception ignored) {
        }
        try {
            return fleetService.getCustomer(id);
        } catch (Exception ignored) {
        }
        return new Location(id, "Location-" + id);
    }
}
