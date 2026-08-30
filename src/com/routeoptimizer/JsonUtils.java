package com.routeoptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {

    public static String toJson(Object obj) {
        if (obj == null) return "null";

        if (obj instanceof String) {
            return "\"" + escape((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return String.valueOf(obj);
        }
        if (obj instanceof CustomerDto) {
            CustomerDto c = (CustomerDto) obj;
            return String.format(Locale.US,
                    "{\"id\":\"%s\",\"name\":\"%s\",\"latitude\":%s,\"longitude\":%s,\"demand\":%.2f,\"priority\":\"%s\",\"serviceTime\":%.2f,\"earliestTime\":%.2f,\"latestTime\":%.2f}",
                    escape(c.getId()), escape(c.getName()), c.getLatitude(), c.getLongitude(),
                    c.getDemand() != null ? c.getDemand() : 0.0,
                    c.getPriority() != null ? c.getPriority() : "MEDIUM",
                    c.getServiceTime() != null ? c.getServiceTime() : 5.0,
                    c.getEarliestTime() != null ? c.getEarliestTime() : 0.0,
                    c.getLatestTime() != null ? c.getLatestTime() : 1440.0);
        }
        if (obj instanceof VehicleDto) {
            VehicleDto v = (VehicleDto) obj;
            return String.format(Locale.US,
                    "{\"id\":\"%s\",\"capacity\":%.2f,\"depotId\":%s,\"fuelConsumptionRate\":%.2f,\"fixedCost\":%.2f}",
                    escape(v.getId()), v.getCapacity() != null ? v.getCapacity() : 0.0,
                    v.getDepotId() != null ? "\"" + escape(v.getDepotId()) + "\"" : "null",
                    v.getFuelConsumptionRate() != null ? v.getFuelConsumptionRate() : 0.12,
                    v.getFixedCost() != null ? v.getFixedCost() : 10.0);
        }
        if (obj instanceof DepotDto) {
            DepotDto d = (DepotDto) obj;
            return String.format(Locale.US,
                    "{\"id\":\"%s\",\"name\":\"%s\",\"latitude\":%s,\"longitude\":%s}",
                    escape(d.getId()), escape(d.getName()), d.getLatitude(), d.getLongitude());
        }
        if (obj instanceof HealthResponse) {
            HealthResponse h = (HealthResponse) obj;
            return String.format(Locale.US,
                    "{\"status\":\"%s\",\"applicationName\":\"%s\",\"version\":\"%s\",\"routingMode\":\"%s\",\"trafficMode\":\"%s\",\"uptimeMs\":%d}",
                    escape(h.getStatus()), escape(h.getApplicationName()), escape(h.getVersion()),
                    escape(h.getRoutingMode()), escape(h.getTrafficMode()), h.getUptimeMs());
        }
        if (obj instanceof ApiErrorResponse) {
            ApiErrorResponse e = (ApiErrorResponse) obj;
            return String.format(Locale.US,
                    "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                    escape(e.getTimestamp()), e.getStatus(), escape(e.getError()), escape(e.getMessage()), escape(e.getPath()));
        }
        if (obj instanceof OptimizationResponse.VehicleRouteResponse) {
            OptimizationResponse.VehicleRouteResponse vr = (OptimizationResponse.VehicleRouteResponse) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append(String.format(Locale.US, "\"vehicleId\":\"%s\",\"depotId\":\"%s\",", escape(vr.getVehicleId()), escape(vr.getDepotId())));
            sb.append("\"customerSequence\":").append(toJson(vr.getCustomerSequence())).append(",");
            sb.append("\"fullRouteLocationIds\":").append(toJson(vr.getFullRouteLocationIds())).append(",");
            sb.append(String.format(Locale.US, "\"totalDistanceKm\":%.2f,\"totalTravelTimeMinutes\":%.2f,\"totalFuelLiters\":%.2f,\"totalCost\":%.2f,\"totalDemand\":%.2f,\"vehicleCapacity\":%.2f,\"capacityViolation\":%.2f,\"timeViolations\":%d",
                    vr.getTotalDistanceKm() != null ? vr.getTotalDistanceKm() : 0.0,
                    vr.getTotalTravelTimeMinutes() != null ? vr.getTotalTravelTimeMinutes() : 0.0,
                    vr.getTotalFuelLiters() != null ? vr.getTotalFuelLiters() : 0.0,
                    vr.getTotalCost() != null ? vr.getTotalCost() : 0.0,
                    vr.getTotalDemand() != null ? vr.getTotalDemand() : 0.0,
                    vr.getVehicleCapacity() != null ? vr.getVehicleCapacity() : 0.0,
                    vr.getCapacityViolation() != null ? vr.getCapacityViolation() : 0.0,
                    vr.getTimeViolations() != null ? vr.getTimeViolations() : 0));
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof OptimizationResponse) {
            OptimizationResponse r = (OptimizationResponse) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append(String.format(Locale.US, "\"optimizationId\":\"%s\",\"status\":\"%s\",", escape(r.getOptimizationId()), escape(r.getStatus())));
            if (r.getErrorMessage() != null) {
                sb.append(String.format(Locale.US, "\"errorMessage\":\"%s\",", escape(r.getErrorMessage())));
            }
            sb.append(String.format(Locale.US, "\"optimizationScore\":%s,\"totalDistanceKm\":%s,\"totalTravelTimeMinutes\":%s,\"totalWaitingTimeMinutes\":%s,\"totalFuelLiters\":%s,\"totalCost\":%s,\"totalCapacityViolations\":%s,\"totalTimeViolations\":%s,\"unassignedCount\":%s,\"duplicateCount\":%s,\"routingProvider\":\"%s\",\"trafficSource\":\"%s\",\"runtimeMs\":%d,",
                    r.getOptimizationScore() != null ? String.format(Locale.US, "%.4f", r.getOptimizationScore()) : "null",
                    r.getTotalDistanceKm() != null ? String.format(Locale.US, "%.2f", r.getTotalDistanceKm()) : "null",
                    r.getTotalTravelTimeMinutes() != null ? String.format(Locale.US, "%.2f", r.getTotalTravelTimeMinutes()) : "null",
                    r.getTotalWaitingTimeMinutes() != null ? String.format(Locale.US, "%.2f", r.getTotalWaitingTimeMinutes()) : "null",
                    r.getTotalFuelLiters() != null ? String.format(Locale.US, "%.2f", r.getTotalFuelLiters()) : "null",
                    r.getTotalCost() != null ? String.format(Locale.US, "%.2f", r.getTotalCost()) : "null",
                    r.getTotalCapacityViolations(),
                    r.getTotalTimeViolations(),
                    r.getUnassignedCount(),
                    r.getDuplicateCount(),
                    escape(r.getRoutingProvider()),
                    escape(r.getTrafficSource()),
                    r.getRuntimeMs() != null ? r.getRuntimeMs() : 0L));
            sb.append("\"vehicleRoutes\":").append(toJson(r.getVehicleRoutes()));
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }

        return "\"" + escape(obj.toString()) + "\"";
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static CustomerDto parseCustomerDto(String json) {
        CustomerDto dto = new CustomerDto();
        dto.setId(extractString(json, "id"));
        dto.setName(extractString(json, "name"));
        dto.setLatitude(extractDouble(json, "latitude"));
        dto.setLongitude(extractDouble(json, "longitude"));
        dto.setDemand(extractDouble(json, "demand"));
        dto.setPriority(extractString(json, "priority"));
        dto.setServiceTime(extractDouble(json, "serviceTime"));
        dto.setEarliestTime(extractDouble(json, "earliestTime"));
        dto.setLatestTime(extractDouble(json, "latestTime"));
        return dto;
    }

    public static VehicleDto parseVehicleDto(String json) {
        VehicleDto dto = new VehicleDto();
        dto.setId(extractString(json, "id"));
        dto.setCapacity(extractDouble(json, "capacity"));
        dto.setDepotId(extractString(json, "depotId"));
        dto.setFuelConsumptionRate(extractDouble(json, "fuelConsumptionRate"));
        dto.setFixedCost(extractDouble(json, "fixedCost"));
        return dto;
    }

    public static DepotDto parseDepotDto(String json) {
        DepotDto dto = new DepotDto();
        dto.setId(extractString(json, "id"));
        dto.setName(extractString(json, "name"));
        dto.setLatitude(extractDouble(json, "latitude"));
        dto.setLongitude(extractDouble(json, "longitude"));
        return dto;
    }

    public static TrafficUpdateRequest parseTrafficUpdateRequest(String json) {
        TrafficUpdateRequest req = new TrafficUpdateRequest();
        req.setOriginId(extractString(json, "originId"));
        req.setDestinationId(extractString(json, "destinationId"));
        req.setOldMultiplier(extractDouble(json, "oldMultiplier"));
        req.setNewMultiplier(extractDouble(json, "newMultiplier"));
        req.setTimestamp(extractLong(json, "timestamp"));
        req.setSource(extractString(json, "source"));
        return req;
    }

    public static OptimizationRequest parseOptimizationRequest(String json) {
        OptimizationRequest req = new OptimizationRequest();
        req.setRoutingMode(extractString(json, "routingMode"));
        req.setTrafficMode(extractString(json, "trafficMode"));
        req.setSeed(extractLong(json, "seed"));
        req.setPopulationSize(extractInteger(json, "populationSize"));
        req.setGenerations(extractInteger(json, "generations"));
        req.setLearningRate(extractDouble(json, "learningRate"));
        req.setExplorationRate(extractDouble(json, "explorationRate"));

        // Parse lists of customers, vehicles, depots
        List<String> custBlocks = extractObjectArray(json, "customers");
        for (String cb : custBlocks) {
            req.getCustomers().add(parseCustomerDto(cb));
        }

        List<String> vehBlocks = extractObjectArray(json, "vehicles");
        for (String vb : vehBlocks) {
            req.getVehicles().add(parseVehicleDto(vb));
        }

        List<String> depBlocks = extractObjectArray(json, "depots");
        for (String db : depBlocks) {
            req.getDepots().add(parseDepotDto(db));
        }

        return req;
    }

    private static String extractString(String json, String key) {
        if (json == null) return null;
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        if (m.find()) return m.group(1);
        return null;
    }

    private static Double extractDouble(String json, String key) {
        if (json == null) return null;
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9.-]+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static Long extractLong(String json, String key) {
        if (json == null) return null;
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9-]+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            try {
                return Long.parseLong(m.group(1));
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static Integer extractInteger(String json, String key) {
        if (json == null) return null;
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9-]+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static List<String> extractObjectArray(String json, String key) {
        List<String> list = new ArrayList<>();
        if (json == null) return list;

        int keyIdx = json.indexOf("\"" + key + "\"");
        if (keyIdx == -1) return list;

        int startBracket = json.indexOf("[", keyIdx);
        if (startBracket == -1) return list;

        int depth = 0;
        int objStart = -1;

        for (int i = startBracket + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == ']') {
                if (depth == 0) break;
            }
            if (c == '{') {
                if (depth == 0) objStart = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && objStart != -1) {
                    list.add(json.substring(objStart, i + 1));
                    objStart = -1;
                }
            }
        }
        return list;
    }
}
