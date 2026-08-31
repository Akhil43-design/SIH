package com.routeoptimizer;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LargeScaleDatasetGenerator {

    public static List<Customer> generateDeterministicDataset(int numCustomers, String city, long seed) {
        Random random = new Random(seed);
        List<Customer> dataset = new ArrayList<>(numCustomers);

        // Approximate bounding boxes for Indian cities (simplified)
        double minLat = 12.8, maxLat = 13.1;
        double minLon = 77.5, maxLon = 77.8; // default to Bengaluru
        
        if (city.equalsIgnoreCase("mumbai")) {
            minLat = 18.9; maxLat = 19.3;
            minLon = 72.8; maxLon = 73.0;
        } else if (city.equalsIgnoreCase("hyderabad")) {
            minLat = 17.3; maxLat = 17.6;
            minLon = 78.3; maxLon = 78.6;
        } else if (city.equalsIgnoreCase("delhi")) {
            minLat = 28.5; maxLat = 28.9;
            minLon = 77.0; maxLon = 77.4;
        }

        for (int i = 0; i < numCustomers; i++) {
            double lat = minLat + (maxLat - minLat) * random.nextDouble();
            double lon = minLon + (maxLon - minLon) * random.nextDouble();
            double demand = 10.0 + random.nextInt(40); // 10 to 50 kg
            
            // Random time windows (e.g. 8 AM to 6 PM)
            int startHour = 8 + random.nextInt(6);
            int endHour = startHour + 2 + random.nextInt(4);
            
            GeoCustomer gc = new GeoCustomer(
                    "CUST_" + city + "_" + i, 
                    "Customer " + i,
                    lat, lon, demand, 
                    DeliveryPriority.MEDIUM,
                    5.0, // service time
                    startHour * 1.0, // earliest
                    endHour * 1.0 // latest
            );
            
            dataset.add(gc);
        }
        
        return dataset;
    }
}
