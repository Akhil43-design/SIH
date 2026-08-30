package com.routeoptimizer;

import java.util.List;
import java.util.Locale;

public class PersistenceIntegrationTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("    PERSISTENCE INTEGRATION TEST");
        System.out.println("========================================");
        System.out.println();

        DatabaseManager db = new DatabaseManager(new DatabaseConfiguration(DatabaseConfiguration.DatabaseType.EMBEDDED_IN_MEMORY, null));
        DepotRepository depotRepo = new DepotRepository(db);
        VehicleRepository vehicleRepo = new VehicleRepository(db);
        CustomerRepository customerRepo = new CustomerRepository(db);

        // 1. Depot CRUD
        DepotEntity d1 = new DepotEntity("W1", "Central Depot", 51.5308, -0.1238);
        depotRepo.save(d1);
        DepotEntity fetchedDepot = depotRepo.findById("W1");
        boolean t1 = fetchedDepot != null && "Central Depot".equals(fetchedDepot.getName());
        System.out.println("Test 1 (Depot Save & Retrieve): " + (t1 ? "PASSED" : "FAILED"));

        // 2. Vehicle CRUD & FK validation
        VehicleEntity v1 = new VehicleEntity("V1", "Van 1", 80.0, 0.12, 10.0, "W1");
        vehicleRepo.save(v1);
        VehicleEntity fetchedVehicle = vehicleRepo.findById("V1");
        boolean t2 = fetchedVehicle != null && fetchedVehicle.getCapacity() == 80.0;
        System.out.println("Test 2 (Vehicle Save & Retrieve): " + (t2 ? "PASSED" : "FAILED"));

        boolean fkCaught = false;
        try {
            vehicleRepo.save(new VehicleEntity("V2", "Van 2", 80.0, 0.12, 10.0, "NON_EXISTENT_DEPOT"));
        } catch (ValidationException e) {
            fkCaught = true;
        }
        System.out.println("Test 3 (Vehicle Foreign Key Validation): " + (fkCaught ? "PASSED" : "FAILED"));

        // 3. Customer CRUD & Constraint Validation
        CustomerEntity c1 = new CustomerEntity("C1", "Westminster", 51.4995, -0.1332, 20.0, "HIGH", 5.0, 10.0, 120.0);
        customerRepo.save(c1);
        CustomerEntity fetchedCust = customerRepo.findById("C1");
        boolean t4 = fetchedCust != null && fetchedCust.getDemand() == 20.0 && !fetchedCust.isCancelled();
        System.out.println("Test 4 (Customer Save & Retrieve): " + (t4 ? "PASSED" : "FAILED"));

        // 4. Dynamic Customer Cancellation (Step 18)
        c1.setCancelled(true);
        customerRepo.save(c1);
        List<CustomerEntity> activeCusts = customerRepo.findAllActiveForOptimization();
        boolean t5 = activeCusts.isEmpty(); // C1 is cancelled so it is excluded from active optimization candidates
        System.out.println("Test 5 (Dynamic Customer Cancellation Flag): " + (t5 ? "PASSED" : "FAILED"));

        // 5. Depot Deletion Protection when referenced (Step 30)
        boolean deleteBlocked = false;
        try {
            depotRepo.deleteById("W1"); // Referenced by V1
        } catch (ApiException e) {
            deleteBlocked = (e.getStatusCode() == 409);
        }
        System.out.println("Test 6 (Depot Referencing Integrity on Delete): " + (deleteBlocked ? "PASSED" : "FAILED"));

        boolean allPassed = t1 && t2 && fkCaught && t4 && t5 && deleteBlocked;

        System.out.println();
        System.out.println("========================================");
        System.out.println("PERSISTENCE INTEGRATION: " + (allPassed ? "PASSED" : "FAILED"));
        System.out.println("========================================");
    }
}
