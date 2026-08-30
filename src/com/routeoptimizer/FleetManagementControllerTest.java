package com.routeoptimizer;

import java.util.List;
import java.util.Locale;

public class FleetManagementControllerTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("   FLEET MANAGEMENT CONTROLLER TEST");
        System.out.println("========================================");
        System.out.println();

        DatabaseManager db = new DatabaseManager(new DatabaseConfiguration(DatabaseConfiguration.DatabaseType.EMBEDDED_IN_MEMORY, null));
        FleetManagementService service = new FleetManagementService(db);
        CustomerController custCtrl = new CustomerController(service);
        VehicleController vehCtrl = new VehicleController(service);
        DepotController depCtrl = new DepotController(service);

        // 1. Depot Tests
        DepotDto d1 = new DepotDto("W1", "North Depot", 51.5308, -0.1238);
        DepotDto createdDepot = depCtrl.createDepot(d1);
        boolean depPass1 = "W1".equals(createdDepot.getId());
        System.out.println("Test 1 (Create Depot): " + (depPass1 ? "PASSED" : "FAILED"));

        boolean depErrPass = false;
        try {
            depCtrl.createDepot(new DepotDto("", "Invalid", 0.0, 0.0));
        } catch (ValidationException e) {
            depErrPass = true;
        }
        System.out.println("Test 2 (Depot Validation Error): " + (depErrPass ? "PASSED" : "FAILED"));

        // 2. Vehicle Tests
        VehicleDto v1 = new VehicleDto("V1", 80.0, "W1", 0.12, 10.0);
        VehicleDto createdVeh = vehCtrl.createVehicle(v1);
        boolean vehPass1 = "V1".equals(createdVeh.getId()) && "W1".equals(createdVeh.getDepotId());
        System.out.println("Test 3 (Create Vehicle): " + (vehPass1 ? "PASSED" : "FAILED"));

        boolean vehErrPass = false;
        try {
            vehCtrl.createVehicle(new VehicleDto("V2", -10.0, "W1", 0.12, 10.0));
        } catch (ValidationException e) {
            vehErrPass = true;
        }
        System.out.println("Test 4 (Vehicle Negative Capacity Validation): " + (vehErrPass ? "PASSED" : "FAILED"));

        // 3. Customer Tests
        CustomerDto c1 = new CustomerDto("C1", "Westminster", 51.4995, -0.1332, 20.0, "HIGH", 5.0, 10.0, 120.0);
        CustomerDto createdCust = custCtrl.createCustomer(c1);
        boolean custPass1 = "C1".equals(createdCust.getId()) && "HIGH".equals(createdCust.getPriority());
        System.out.println("Test 5 (Create Customer): " + (custPass1 ? "PASSED" : "FAILED"));

        boolean custErrPass = false;
        try {
            custCtrl.createCustomer(new CustomerDto("C2", "Invalid Window", 51.0, 0.0, 10.0, "LOW", 5.0, 100.0, 50.0));
        } catch (ValidationException e) {
            custErrPass = true;
        }
        System.out.println("Test 6 (Customer Inverted Time Window Validation): " + (custErrPass ? "PASSED" : "FAILED"));

        // 4. Update & Delete
        c1.setDemand(25.0);
        CustomerDto updatedCust = custCtrl.updateCustomer("C1", c1);
        boolean updatePass = updatedCust.getDemand() == 25.0;
        System.out.println("Test 7 (Update Customer): " + (updatePass ? "PASSED" : "FAILED"));

        boolean deletePass = custCtrl.deleteCustomer("C1");
        System.out.println("Test 8 (Delete Customer): " + (deletePass ? "PASSED" : "FAILED"));

        boolean allPassed = depPass1 && depErrPass && vehPass1 && vehErrPass && custPass1 && custErrPass && updatePass && deletePass;

        System.out.println();
        System.out.println("========================================");
        System.out.println("FLEET MANAGEMENT CONTROLLER: " + (allPassed ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
