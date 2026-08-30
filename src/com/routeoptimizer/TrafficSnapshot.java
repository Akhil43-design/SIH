package com.routeoptimizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TrafficSnapshot implements TrafficDataProvider {

    private final Map<String, TrafficMetrics> snapshotData;
    private final String snapshotName;

    public TrafficSnapshot(String snapshotName, Map<String, TrafficMetrics> snapshotData) {
        this.snapshotName = snapshotName != null ? snapshotName : "DefaultSnapshot";
        this.snapshotData = new HashMap<>(snapshotData);
    }

    public TrafficSnapshot(String snapshotName) {
        this(snapshotName, new HashMap<>());
    }

    public static void saveSnapshot(Map<String, TrafficMetrics> data, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("# TrafficSnapshot\n");
            writer.write("# origin_id->dest_id;multiplier;speed_kmh;delay_min;timestamp;source\n");

            for (Map.Entry<String, TrafficMetrics> entry : data.entrySet()) {
                String key = entry.getKey();
                TrafficMetrics m = entry.getValue();
                writer.write(String.format(Locale.US, "%s;%.4f;%.2f;%.2f;%d;%s%n",
                        key, m.getMultiplier(), m.getSpeedKmh(), m.getDelayMinutes(), m.getTimestamp(), m.getSource()));
            }
        }
    }

    public static TrafficSnapshot loadSnapshot(String filePath) throws IOException {
        Map<String, TrafficMetrics> data = new HashMap<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return new TrafficSnapshot(file.getName(), data);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length >= 6) {
                    String key = parts[0];
                    double mult = Double.parseDouble(parts[1]);
                    double speed = Double.parseDouble(parts[2]);
                    double delay = Double.parseDouble(parts[3]);
                    long ts = Long.parseLong(parts[4]);
                    String src = parts[5];

                    data.put(key, new TrafficMetrics(mult, speed, delay, ts, src, false));
                }
            }
        }
        return new TrafficSnapshot(file.getName(), data);
    }

    @Override
    public TrafficMetrics getTraffic(Location origin, Location destination, long timestampMillis) {
        if (origin == null || destination == null) {
            return TrafficMetrics.createSimulated(1.0, 10.0, timestampMillis);
        }
        String key = origin.getId() + "->" + destination.getId();
        TrafficMetrics tm = snapshotData.get(key);
        if (tm != null) {
            return tm;
        }
        return TrafficMetrics.createSimulated(1.0, 10.0, timestampMillis);
    }

    @Override
    public double getAdjustedTravelTime(Road road, double baseTravelTimeMinutes, long timestampMillis) {
        if (road == null) {
            return baseTravelTimeMinutes;
        }
        TrafficMetrics tm = getTraffic(road.getFrom(), road.getTo(), timestampMillis);
        return baseTravelTimeMinutes * tm.getMultiplier();
    }

    @Override
    public TrafficSourceMode getMode() {
        return TrafficSourceMode.SNAPSHOT;
    }

    @Override
    public String getSourceName() {
        return "TrafficSnapshot [" + snapshotName + "]";
    }

    @Override
    public boolean isAvailable() {
        return !snapshotData.isEmpty();
    }

    public void putMetric(String fromId, String toId, TrafficMetrics metrics) {
        snapshotData.put(fromId + "->" + toId, metrics);
    }
}
