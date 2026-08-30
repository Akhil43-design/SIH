package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class VehicleController {

    private final FleetManagementService fleetService;

    public VehicleController(FleetManagementService fleetService) {
        this.fleetService = fleetService != null ? fleetService : new FleetManagementService();
    }

    public VehicleDto createVehicle(VehicleDto dto) {
        Vehicle v = fleetService.createVehicle(dto);
        return VehicleDto.fromDomain(v);
    }

    public List<VehicleDto> getAllVehicles() {
        List<VehicleDto> list = new ArrayList<>();
        for (Vehicle v : fleetService.getAllVehicles()) {
            list.add(VehicleDto.fromDomain(v));
        }
        return list;
    }

    public VehicleDto getVehicle(String id) {
        Vehicle v = fleetService.getVehicle(id);
        return VehicleDto.fromDomain(v);
    }

    public VehicleDto updateVehicle(String id, VehicleDto dto) {
        Vehicle updated = fleetService.updateVehicle(id, dto);
        return VehicleDto.fromDomain(updated);
    }

    public boolean deleteVehicle(String id) {
        return fleetService.deleteVehicle(id);
    }
}
