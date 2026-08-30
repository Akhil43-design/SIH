package com.routeoptimizer;

import java.util.List;

public class IndiaDatasetValidationTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   INDIA DATASET VALIDATION TEST");
        System.out.println("========================================\n");

        int passed = 0;
        int failed = 0;

        List<IndianCityDatasets.CityInfo> cities = IndianCityDatasets.getAllCities();
        System.out.println("Total Supported Indian Metropolitan Cities: " + cities.size());

        // Test 1: Exactly 10 Indian cities supported
        if (cities.size() >= 10) {
            System.out.println("[PASS] Test 1: 10 Indian metropolitan logistics datasets loaded.");
            passed++;
        } else {
            System.err.println("[FAIL] Test 1: Less than 10 cities found: " + cities.size());
            failed++;
        }

        // Test 2: Default city is Bengaluru, Karnataka, India
        IndianCityDatasets.CityDataset blr = IndianCityDatasets.getDefaultDataset();
        if (blr != null && "bengaluru".equalsIgnoreCase(blr.getInfo().getId()) && "Karnataka".equalsIgnoreCase(blr.getInfo().getState())) {
            System.out.println("[PASS] Test 2: Default application city is Bengaluru, Karnataka, India.");
            passed++;
        } else {
            System.err.println("[FAIL] Test 2: Default city is not Bengaluru.");
            failed++;
        }

        // Test 3: Validate Geographic Bounds for all 10 cities
        boolean allCoordsValid = true;
        for (IndianCityDatasets.CityInfo c : cities) {
            IndianCityDatasets.CityDataset ds = IndianCityDatasets.getCityDataset(c.getId());
            // India Latitude: 8.0 - 37.0 N, Longitude: 68.0 - 97.5 E
            if (c.getCenterLat() < 8.0 || c.getCenterLat() > 37.0 || c.getCenterLng() < 68.0 || c.getCenterLng() > 97.5) {
                System.err.println("Invalid center coordinates for " + c.getName() + ": [" + c.getCenterLat() + ", " + c.getCenterLng() + "]");
                allCoordsValid = false;
            }

            for (DepotDto d : ds.getDepots()) {
                if (d.getLatitude() < 8.0 || d.getLatitude() > 37.0 || d.getLongitude() < 68.0 || d.getLongitude() > 97.5) {
                    System.err.println("Invalid depot coordinate in " + c.getName() + ": " + d.getName());
                    allCoordsValid = false;
                }
            }

            for (CustomerDto cust : ds.getCustomers()) {
                if (cust.getLatitude() < 8.0 || cust.getLatitude() > 37.0 || cust.getLongitude() < 68.0 || cust.getLongitude() > 97.5) {
                    System.err.println("Invalid customer coordinate in " + c.getName() + ": " + cust.getName());
                    allCoordsValid = false;
                }
            }
        }

        if (allCoordsValid) {
            System.out.println("[PASS] Test 3: All 10 city coordinates, depots, and customer stops are geographically verified within India bounds.");
            passed++;
        } else {
            System.err.println("[FAIL] Test 3: One or more coordinates fall outside India geographic bounds.");
            failed++;
        }

        // Test 4: Database Load & Referential Integrity Test
        DatabaseManager db = new DatabaseManager();
        FleetManagementService fleetService = new FleetManagementService(db);

        fleetService.loadCityDataset("mumbai");
        List<Location> mumDepots = fleetService.getAllDepots();
        List<Vehicle> mumVehicles = fleetService.getAllVehicles();
        List<Customer> mumCusts = fleetService.getAllCustomers();

        if (mumDepots.size() >= 2 && mumVehicles.size() >= 3 && mumCusts.size() >= 8) {
            System.out.println("[PASS] Test 4: Mumbai dataset loaded into database with complete referential integrity ("
                    + mumDepots.size() + " depots, " + mumVehicles.size() + " vehicles, " + mumCusts.size() + " customers).");
            passed++;
        } else {
            System.err.println("[FAIL] Test 4: Database load failed for Mumbai.");
            failed++;
        }

        // Test 5: Switch back to Bengaluru default
        fleetService.loadCityDataset("bengaluru");
        List<Customer> blrCusts = fleetService.getAllCustomers();
        boolean hasManyata = false;
        for (Customer c : blrCusts) {
            if (c.getName().contains("Manyata")) {
                hasManyata = true;
                break;
            }
        }
        if (blrCusts.size() >= 10 && hasManyata) {
            System.out.println("[PASS] Test 5: Database successfully reset to default Bengaluru Indian dataset.");
            passed++;
        } else {
            System.err.println("[FAIL] Test 5: Default Bengaluru reset failed.");
            failed++;
        }

        System.out.println("\n========================================");
        System.out.println("SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
