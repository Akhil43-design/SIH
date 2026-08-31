package com.routeoptimizer;

import java.util.ArrayList;
import java.util.List;

public class LargeScaleCustomerPartitioner {

    /**
     * Partitions a large list of customers into manageable geographic clusters based on a grid.
     */
    public List<CustomerCluster> partition(List<Customer> customers, int maxClusterSize, String city) {
        List<CustomerCluster> clusters = new ArrayList<>();
        
        if (customers.isEmpty()) return clusters;

        // Determine bounding box of all customers
        double minLat = 90.0, maxLat = -90.0;
        double minLon = 180.0, maxLon = -180.0;
        
        for (Customer c : customers) {
            if (c instanceof GeoCustomer) {
                GeoCustomer gc = (GeoCustomer) c;
                if (gc.getLatitude() < minLat) minLat = gc.getLatitude();
                if (gc.getLatitude() > maxLat) maxLat = gc.getLatitude();
                if (gc.getLongitude() < minLon) minLon = gc.getLongitude();
                if (gc.getLongitude() > maxLon) maxLon = gc.getLongitude();
            }
        }
        
        // Simple grid partitioning heuristic
        int totalCustomers = customers.size();
        int estimatedClusters = (int) Math.ceil((double) totalCustomers / maxClusterSize);
        if (estimatedClusters <= 0) estimatedClusters = 1;
        
        int gridDim = (int) Math.ceil(Math.sqrt(estimatedClusters));
        if (gridDim == 0) gridDim = 1;
        
        double latStep = (maxLat - minLat) / gridDim;
        double lonStep = (maxLon - minLon) / gridDim;
        
        // Handle case where all customers are at the exact same location
        if (latStep == 0) latStep = 0.01;
        if (lonStep == 0) lonStep = 0.01;
        
        CustomerCluster[][] grid = new CustomerCluster[gridDim][gridDim];
        for (int i = 0; i < gridDim; i++) {
            for (int j = 0; j < gridDim; j++) {
                grid[i][j] = new CustomerCluster("Cluster_" + city + "_" + i + "_" + j, city);
                clusters.add(grid[i][j]);
            }
        }
        
        for (Customer c : customers) {
            if (c instanceof GeoCustomer) {
                GeoCustomer gc = (GeoCustomer) c;
                
                int i = (int) ((gc.getLatitude() - minLat) / latStep);
                int j = (int) ((gc.getLongitude() - minLon) / lonStep);
                
                if (i >= gridDim) i = gridDim - 1;
                if (j >= gridDim) j = gridDim - 1;
                if (i < 0) i = 0;
                if (j < 0) j = 0;
                
                grid[i][j].addCustomer(c);
            } else {
                // Non-geo customers fall back to cluster 0,0
                grid[0][0].addCustomer(c);
            }
        }
        
        // Remove empty clusters and recalculate centroids
        clusters.removeIf(c -> c.size() == 0);
        for (CustomerCluster cluster : clusters) {
            cluster.calculateCentroid();
        }
        
        return splitOversizedClusters(clusters, maxClusterSize, city);
    }
    
    private List<CustomerCluster> splitOversizedClusters(List<CustomerCluster> clusters, int maxClusterSize, String city) {
        List<CustomerCluster> finalClusters = new ArrayList<>();
        int splitId = 0;
        for (CustomerCluster cluster : clusters) {
            if (cluster.size() > maxClusterSize) {
                // Arbitrary linear split to ensure strictly abiding by maxClusterSize
                List<Customer> allCust = cluster.getCustomers();
                for (int i = 0; i < allCust.size(); i += maxClusterSize) {
                    CustomerCluster subCluster = new CustomerCluster(cluster.getClusterId() + "_sub_" + (++splitId), city);
                    int end = Math.min(i + maxClusterSize, allCust.size());
                    for (int j = i; j < end; j++) {
                        subCluster.addCustomer(allCust.get(j));
                    }
                    subCluster.calculateCentroid();
                    finalClusters.add(subCluster);
                }
            } else {
                finalClusters.add(cluster);
            }
        }
        return finalClusters;
    }
}
