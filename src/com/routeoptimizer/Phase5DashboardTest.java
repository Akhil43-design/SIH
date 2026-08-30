package com.routeoptimizer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public class Phase5DashboardTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("       PHASE 5 DASHBOARD TEST");
        System.out.println("========================================");
        System.out.println();

        int testPort = 8092;
        DatabaseManager testDb = new DatabaseManager(
                new DatabaseConfiguration(DatabaseConfiguration.DatabaseType.EMBEDDED_IN_MEMORY, null)
        );
        ServerConfiguration serverConfig = new ServerConfiguration(testPort, "*", "REAL_OSRM", "SIMULATED");
        RestApiServer server = new RestApiServer(serverConfig, testDb);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        boolean allPassed = false;
        try {
            server.start();
            System.out.println("Embedded Server started on port " + testPort + " for Dashboard Integration.");
            String baseUrl = "http://localhost:" + testPort + "/api/v1";

            // 1. Dashboard loads & Health is UP
            HttpRequest healthReq = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/health")).GET().build();
            HttpResponse<String> healthResp = client.send(healthReq, HttpResponse.BodyHandlers.ofString());
            boolean t1 = healthResp.statusCode() == 200 && healthResp.body().contains("\"UP\"");
            System.out.println("Test 1 (Dashboard Health API): " + (t1 ? "PASSED" : "FAILED"));

            // 2. Setup Multi-Depot Fleet via API
            String depot1 = "{\"id\":\"W1\",\"name\":\"North Hub\",\"latitude\":51.5308,\"longitude\":-0.1238}";
            String depot2 = "{\"id\":\"W2\",\"name\":\"South Hub\",\"latitude\":51.5055,\"longitude\":-0.0863}";
            client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/depots")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(depot1)).build(), HttpResponse.BodyHandlers.ofString());
            client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/depots")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(depot2)).build(), HttpResponse.BodyHandlers.ofString());

            String veh1 = "{\"id\":\"V1\",\"capacity\":80.0,\"depotId\":\"W1\",\"fuelConsumptionRate\":0.12,\"fixedCost\":10.0}";
            String veh2 = "{\"id\":\"V2\",\"capacity\":90.0,\"depotId\":\"W2\",\"fuelConsumptionRate\":0.12,\"fixedCost\":10.0}";
            client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/vehicles")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(veh1)).build(), HttpResponse.BodyHandlers.ofString());
            client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/vehicles")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(veh2)).build(), HttpResponse.BodyHandlers.ofString());

            String cust1 = "{\"id\":\"C1\",\"name\":\"Westminster\",\"latitude\":51.4995,\"longitude\":-0.1332,\"demand\":20.0,\"priority\":\"HIGH\",\"serviceTime\":5.0,\"earliestTime\":10.0,\"latestTime\":180.0}";
            String cust2 = "{\"id\":\"C2\",\"name\":\"Covent Garden\",\"latitude\":51.5117,\"longitude\":-0.1240,\"demand\":25.0,\"priority\":\"MEDIUM\",\"serviceTime\":5.0,\"earliestTime\":10.0,\"latestTime\":180.0}";
            String cust3 = "{\"id\":\"C3\",\"name\":\"Canary Wharf\",\"latitude\":51.5054,\"longitude\":-0.0209,\"demand\":30.0,\"priority\":\"HIGH\",\"serviceTime\":5.0,\"earliestTime\":10.0,\"latestTime\":180.0}";
            String cust4 = "{\"id\":\"C4\",\"name\":\"Camden Town\",\"latitude\":51.5390,\"longitude\":-0.1426,\"demand\":20.0,\"priority\":\"LOW\",\"serviceTime\":5.0,\"earliestTime\":10.0,\"latestTime\":180.0}";
            client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/customers")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(cust1)).build(), HttpResponse.BodyHandlers.ofString());
            client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/customers")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(cust2)).build(), HttpResponse.BodyHandlers.ofString());
            client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/customers")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(cust3)).build(), HttpResponse.BodyHandlers.ofString());
            client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/customers")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(cust4)).build(), HttpResponse.BodyHandlers.ofString());

            HttpResponse<String> getCusts = client.send(HttpRequest.newBuilder().uri(URI.create(baseUrl + "/customers")).GET().build(), HttpResponse.BodyHandlers.ofString());
            boolean t2 = getCusts.statusCode() == 200 && getCusts.body().contains("Westminster");
            System.out.println("Test 2 (Fleet Data Setup & Customer Retrieval): " + (t2 ? "PASSED" : "FAILED"));

            // 3. Optimization executes & generates Fleet Routes & KPI Metrics
            String optPayload = "{\"populationSize\":30,\"generations\":40,\"seed\":42}";
            HttpRequest optReq = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/optimization/run")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(optPayload)).build();
            HttpResponse<String> optResp = client.send(optReq, HttpResponse.BodyHandlers.ofString());
            boolean t3 = optResp.statusCode() == 200 && optResp.body().contains("\"COMPLETED\"") && optResp.body().contains("\"totalDistanceKm\"");
            System.out.println("Test 3 (QIGA Optimization Execution & Metrics): " + (t3 ? "PASSED" : "FAILED"));

            String optId = "opt-1";
            int idIdx = optResp.body().indexOf("\"optimizationId\":\"");
            if (idIdx != -1) {
                int endIdx = optResp.body().indexOf("\"", idIdx + 18);
                optId = optResp.body().substring(idIdx + 18, endIdx);
            }

            // 4. Traffic Event Injection Demo
            String trafficEvent = "{\"originId\":\"W1\",\"destinationId\":\"C1\",\"oldMultiplier\":1.0,\"newMultiplier\":3.0,\"source\":\"TRAFFIC_SURGE_DEMO\"}";
            HttpRequest trafficReq = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/traffic/update")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(trafficEvent)).build();
            HttpResponse<String> trafficResp = client.send(trafficReq, HttpResponse.BodyHandlers.ofString());
            boolean t4 = trafficResp.statusCode() == 200 && trafficResp.body().contains("\"UPDATED\"");
            System.out.println("Test 4 (Traffic Event Injection): " + (t4 ? "PASSED" : "FAILED"));

            // 5. Dynamic Fleet Re-Optimization Demo
            HttpRequest reoptReq = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/optimization/" + optId + "/reoptimize")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(trafficEvent)).build();
            HttpResponse<String> reoptResp = client.send(reoptReq, HttpResponse.BodyHandlers.ofString());
            boolean t5 = reoptResp.statusCode() == 200 && reoptResp.body().contains("\"COMPLETED\"");
            System.out.println("Test 5 (Dynamic Fleet Re-Optimization): " + (t5 ? "PASSED" : "FAILED"));

            // 6. Optimization History & Audit Revisions
            HttpRequest histReq = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/optimization")).GET().build();
            HttpResponse<String> histResp = client.send(histReq, HttpResponse.BodyHandlers.ofString());
            boolean t6 = histResp.statusCode() == 200 && histResp.body().contains(optId);
            System.out.println("Test 6 (Optimization History & Audit Records): " + (t6 ? "PASSED" : "FAILED"));

            allPassed = t1 && t2 && t3 && t4 && t5 && t6;

            System.out.println();
            System.out.println("========================================");
            System.out.println("PHASE 5 DASHBOARD TEST: " + (allPassed ? "PASSED" : "FAILED"));
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("Phase 5 Test Failure: " + e.getMessage());
            e.printStackTrace();
        } finally {
            server.stop();
            System.out.println("Embedded Server stopped.");
        }
    }
}
