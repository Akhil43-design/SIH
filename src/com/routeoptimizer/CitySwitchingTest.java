package com.routeoptimizer;

import java.util.List;

public class CitySwitchingTest {

    public static void main(String[] args) {
        System.out.println("Running CitySwitchingTest...");
        
        List<IndianCityDatasets.CityInfo> cities = IndianCityDatasets.getAllCities();
        if (cities.size() != 20) {
            System.err.println("FAILED: Expected 20 cities, found " + cities.size());
            System.exit(1);
        }
        System.out.println("Found exactly 20 cities in the dataset.");

        DatabaseManager db = new DatabaseManager();
        FleetManagementService fleetService = new FleetManagementService(db);
        
        try {
            // Load a new city
            IndianCityDatasets.CityDataset dataset = fleetService.loadCityDataset("visakhapatnam");
            if (dataset == null) {
                System.err.println("FAILED: Visakhapatnam dataset is null");
                System.exit(1);
            }
            if (!dataset.getInfo().getId().equals("visakhapatnam")) {
                System.err.println("FAILED: Dataset ID mismatch. Expected visakhapatnam, got " + dataset.getInfo().getId());
                System.exit(1);
            }
            System.out.println("Successfully loaded Visakhapatnam dataset.");
            
            // Validate limits
            if (dataset.getCustomers().isEmpty()) {
                System.err.println("FAILED: Customers are empty");
                System.exit(1);
            }
            System.out.println("Customers verified.");
            
            System.out.println("CitySwitchingTest PASSED.");
        } catch (Exception e) {
            System.err.println("FAILED: Exception during execution");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
