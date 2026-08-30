package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class DepotController {

    private final FleetManagementService fleetService;

    public DepotController(FleetManagementService fleetService) {
        this.fleetService = fleetService != null ? fleetService : new FleetManagementService();
    }

    public DepotDto createDepot(DepotDto dto) {
        Location d = fleetService.createDepot(dto);
        return DepotDto.fromDomain(d);
    }

    public List<DepotDto> getAllDepots() {
        List<DepotDto> list = new ArrayList<>();
        for (Location d : fleetService.getAllDepots()) {
            list.add(DepotDto.fromDomain(d));
        }
        return list;
    }

    public DepotDto getDepot(String id) {
        Location d = fleetService.getDepot(id);
        return DepotDto.fromDomain(d);
    }

    public DepotDto updateDepot(String id, DepotDto dto) {
        Location updated = fleetService.updateDepot(id, dto);
        return DepotDto.fromDomain(updated);
    }

    public boolean deleteDepot(String id) {
        return fleetService.deleteDepot(id);
    }
}
