package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class CustomerController {

    private final FleetManagementService fleetService;

    public CustomerController(FleetManagementService fleetService) {
        this.fleetService = fleetService != null ? fleetService : new FleetManagementService();
    }

    public CustomerDto createCustomer(CustomerDto dto) {
        Customer c = fleetService.createCustomer(dto);
        return CustomerDto.fromDomain(c);
    }

    public List<CustomerDto> getAllCustomers() {
        List<CustomerDto> list = new ArrayList<>();
        for (Customer c : fleetService.getAllCustomers()) {
            list.add(CustomerDto.fromDomain(c));
        }
        return list;
    }

    public CustomerDto getCustomer(String id) {
        Customer c = fleetService.getCustomer(id);
        return CustomerDto.fromDomain(c);
    }

    public CustomerDto updateCustomer(String id, CustomerDto dto) {
        Customer updated = fleetService.updateCustomer(id, dto);
        return CustomerDto.fromDomain(updated);
    }

    public boolean deleteCustomer(String id) {
        return fleetService.deleteCustomer(id);
    }
}
