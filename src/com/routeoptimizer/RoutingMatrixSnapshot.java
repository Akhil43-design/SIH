package com.routeoptimizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class RoutingMatrixSnapshot {

    public static void saveSnapshot(RoutingCache cache, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("# RoutingMatrixSnapshot\n");
            writer.write("# origin_lat,origin_lon->dest_lat,dest_lon;distance_km;time_min;geometry\n");

            for (Map.Entry<String, RouteMetrics> entry : cache.getCacheEntries().entrySet()) {
                String key = entry.getKey();
                RouteMetrics m = entry.getValue();
                writer.write(String.format(Locale.US, "%s;%.5f;%.5f;%s%n",
                        key, m.getDistanceKm(), m.getTravelTimeMinutes(), m.getGeometry()));
            }
        }
    }

    public static RoutingCache loadSnapshot(String filePath) throws IOException {
        RoutingCache cache = new RoutingCache();
        File file = new File(filePath);
        if (!file.exists()) {
            return cache;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    String key = parts[0];
                    double dist = Double.parseDouble(parts[1]);
                    double time = Double.parseDouble(parts[2]);
                    String geom = parts.length > 3 ? parts[3] : "";

                    String[] keyParts = key.split("->");
                    if (keyParts.length == 2) {
                        String[] orig = keyParts[0].split(",");
                        String[] dest = keyParts[1].split(",");
                        if (orig.length == 2 && dest.length == 2) {
                            GeoLocation o = new GeoLocation("O", Double.parseDouble(orig[0]), Double.parseDouble(orig[1]));
                            GeoLocation d = new GeoLocation("D", Double.parseDouble(dest[0]), Double.parseDouble(dest[1]));
                            cache.put(o, d, new RouteMetrics(dist, time, geom));
                        }
                    }
                }
            }
        }
        return cache;
    }
}
