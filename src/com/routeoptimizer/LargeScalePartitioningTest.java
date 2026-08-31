package com.routeoptimizer;

import java.util.List;

public class LargeScalePartitioningTest {

    public static void main(String[] args) {
        System.out.println("Running LargeScalePartitioningTest...");
        LargeScaleCustomerPartitioner partitioner = new LargeScaleCustomerPartitioner();
        List<Customer> dataset = LargeScaleDatasetGenerator.generateDeterministicDataset(1000, "bengaluru", 1L);
        
        int maxClusterSize = 100;
        List<CustomerCluster> clusters = partitioner.partition(dataset, maxClusterSize, "bengaluru");
        
        if (clusters == null || clusters.isEmpty()) {
            throw new AssertionError("Clusters are empty");
        }
        
        int totalCustomers = 0;
        for (CustomerCluster cluster : clusters) {
            if (cluster.size() == 0) throw new AssertionError("Cluster should not be empty");
            if (cluster.size() > maxClusterSize) throw new AssertionError("Cluster size " + cluster.size() + " exceeds maximum " + maxClusterSize);
            if (cluster.getClusterId() == null) throw new AssertionError("Cluster ID is null");
            if (cluster.centroid() == null) throw new AssertionError("Cluster centroid is null");
            totalCustomers += cluster.size();
        }
        
        if (totalCustomers != 1000) {
            throw new AssertionError("All customers must be assigned to exactly one cluster");
        }
        
        System.out.println("LargeScalePartitioningTest: PASS");
    }
}
