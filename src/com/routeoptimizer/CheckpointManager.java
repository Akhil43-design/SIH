package com.routeoptimizer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CheckpointManager {

    private final String checkpointDir;

    public CheckpointManager(String checkpointDir) {
        this.checkpointDir = checkpointDir;
        new File(checkpointDir).mkdirs();
    }

    public void saveCheckpoint(String runId, List<FleetRoutePlan> completedSubPlans) {
        File file = new File(checkpointDir, runId + ".chk");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(completedSubPlans);
            System.out.println("Saved checkpoint for " + runId + " with " + completedSubPlans.size() + " completed clusters.");
        } catch (IOException e) {
            System.err.println("Failed to save checkpoint: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<FleetRoutePlan> loadCheckpoint(String runId) {
        File file = new File(checkpointDir, runId + ".chk");
        if (!file.exists()) return new ArrayList<>();
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<FleetRoutePlan>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load checkpoint: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void clearCheckpoint(String runId) {
        File file = new File(checkpointDir, runId + ".chk");
        if (file.exists()) {
            file.delete();
        }
    }
}
