package com.routeoptimizer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public class ApiEndToEndTest {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        System.out.println("========================================");
        System.out.println("      REST API END-TO-END TEST");
        System.out.println("========================================");
        System.out.println();

        // Start Server on Port 8089 with clean in-memory database for isolated test execution
        int testPort = 8089;
        DatabaseManager testDb = new DatabaseManager(
                new DatabaseConfiguration(DatabaseConfiguration.DatabaseType.EMBEDDED_IN_MEMORY, null)
        );
        ServerConfiguration serverConfig = new ServerConfiguration(testPort, "*", "SYNTHETIC", "SIMULATED");
        RestApiServer server = new RestApiServer(serverConfig, testDb);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        boolean allPassed = false;
        try {
            server.start();
            System.out.println("Embedded REST API Server started on port " + testPort);
            String baseUrl = "http://localhost:" + testPort + "/api/v1";

            // 1. Test GET /api/v1/health
            HttpRequest healthReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/health"))
                    .GET()
                    .build();
            HttpResponse<String> healthResp = client.send(healthReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("1. GET /api/v1/health -> Status: " + healthResp.statusCode() + ", Body: " + healthResp.body());
            boolean t1 = healthResp.statusCode() == 200 && healthResp.body().contains("\"UP\"");

            // 2. Test POST /api/v1/depots
            String depotJson = "{\"id\":\"W1\",\"name\":\"Central Logistic Hub\",\"latitude\":51.5308,\"longitude\":-0.1238}";
            HttpRequest createDepotReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/depots"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(depotJson))
                    .build();
            HttpResponse<String> depotResp = client.send(createDepotReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("2. POST /api/v1/depots -> Status: " + depotResp.statusCode());
            boolean t2 = depotResp.statusCode() == 201;

            // 3. Test POST /api/v1/vehicles
            String vehicleJson = "{\"id\":\"V1\",\"capacity\":80.0,\"depotId\":\"W1\",\"fuelConsumptionRate\":0.12,\"fixedCost\":10.0}";
            HttpRequest createVehReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/vehicles"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(vehicleJson))
                    .build();
            HttpResponse<String> vehResp = client.send(createVehReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("3. POST /api/v1/vehicles -> Status: " + vehResp.statusCode());
            boolean t3 = vehResp.statusCode() == 201;

            // 4. Test POST /api/v1/customers
            String custJson = "{\"id\":\"C1\",\"name\":\"Westminster\",\"latitude\":51.4995,\"longitude\":-0.1332,\"demand\":20.0,\"priority\":\"HIGH\",\"serviceTime\":5.0,\"earliestTime\":10.0,\"latestTime\":120.0}";
            HttpRequest createCustReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/customers"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(custJson))
                    .build();
            HttpResponse<String> custResp = client.send(createCustReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("4. POST /api/v1/customers -> Status: " + custResp.statusCode());
            boolean t4 = custResp.statusCode() == 201;

            // 5. Test POST /api/v1/optimization/run
            String optJson = "{"
                    + "\"depots\":[{\"id\":\"W1\",\"name\":\"Hub 1\",\"latitude\":51.5308,\"longitude\":-0.1238}],"
                    + "\"vehicles\":[{\"id\":\"V1\",\"capacity\":80.0,\"depotId\":\"W1\"},{\"id\":\"V2\",\"capacity\":80.0,\"depotId\":\"W1\"}],"
                    + "\"customers\":["
                    + "{\"id\":\"C1\",\"name\":\"Cust 1\",\"latitude\":51.51,\"longitude\":-0.12,\"demand\":20.0,\"priority\":\"HIGH\"},"
                    + "{\"id\":\"C2\",\"name\":\"Cust 2\",\"latitude\":51.52,\"longitude\":-0.11,\"demand\":25.0,\"priority\":\"MEDIUM\"},"
                    + "{\"id\":\"C3\",\"name\":\"Cust 3\",\"latitude\":51.50,\"longitude\":-0.14,\"demand\":15.0,\"priority\":\"LOW\"}"
                    + "],"
                    + "\"populationSize\":30,\"generations\":40,\"seed\":100"
                    + "}";

            HttpRequest runOptReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/optimization/run"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(optJson))
                    .build();
            HttpResponse<String> optResp = client.send(runOptReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("5. POST /api/v1/optimization/run -> Status: " + optResp.statusCode() + ", Body Length: " + optResp.body().length());
            boolean t5 = optResp.statusCode() == 200 && optResp.body().contains("\"COMPLETED\"");

            // Extract optimizationId
            String optId = "opt-1";
            int idIdx = optResp.body().indexOf("\"optimizationId\":\"");
            if (idIdx != -1) {
                int endIdx = optResp.body().indexOf("\"", idIdx + 18);
                optId = optResp.body().substring(idIdx + 18, endIdx);
            }

            // 6. Test GET /api/v1/optimization/{id}
            HttpRequest getOptReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/optimization/" + optId))
                    .GET()
                    .build();
            HttpResponse<String> getOptResp = client.send(getOptReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("6. GET /api/v1/optimization/" + optId + " -> Status: " + getOptResp.statusCode());
            boolean t6 = getOptResp.statusCode() == 200;

            // 7. Test GET /api/v1/optimization (History endpoint)
            HttpRequest getHistoryReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/optimization"))
                    .GET()
                    .build();
            HttpResponse<String> getHistoryResp = client.send(getHistoryReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("7. GET /api/v1/optimization -> Status: " + getHistoryResp.statusCode());
            boolean t7 = getHistoryResp.statusCode() == 200 && getHistoryResp.body().contains(optId);

            // 8. Test POST /api/v1/traffic/update
            String trafficUpdateJson = "{\"originId\":\"W1\",\"destinationId\":\"C1\",\"oldMultiplier\":1.0,\"newMultiplier\":2.5,\"source\":\"SIMULATED_TEST\"}";
            HttpRequest trafficReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/traffic/update"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(trafficUpdateJson))
                    .build();
            HttpResponse<String> trafficResp = client.send(trafficReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("8. POST /api/v1/traffic/update -> Status: " + trafficResp.statusCode());
            boolean t8 = trafficResp.statusCode() == 200;

            // 9. Test POST /api/v1/optimization/{id}/reoptimize
            HttpRequest reoptReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/optimization/" + optId + "/reoptimize"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(trafficUpdateJson))
                    .build();
            HttpResponse<String> reoptResp = client.send(reoptReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("9. POST /api/v1/optimization/" + optId + "/reoptimize -> Status: " + reoptResp.statusCode());
            boolean t9 = reoptResp.statusCode() == 200;

            // 10. Test Error Handling 404 (Not Found)
            HttpRequest notFoundReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/customers/NON_EXISTENT_ID"))
                    .GET()
                    .build();
            HttpResponse<String> notFoundResp = client.send(notFoundReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("10. GET /api/v1/customers/NON_EXISTENT_ID -> Status: " + notFoundResp.statusCode() + " (Expected 404)");
            boolean t10 = notFoundResp.statusCode() == 404 && notFoundResp.body().contains("\"NOT_FOUND\"");

            // 11. Test Error Handling 400 (Validation Failure)
            String invalidCustJson = "{\"id\":\"\",\"name\":\"No ID Customer\"}";
            HttpRequest badReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/customers"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(invalidCustJson))
                    .build();
            HttpResponse<String> badResp = client.send(badReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("11. POST /api/v1/customers (Invalid) -> Status: " + badResp.statusCode() + " (Expected 400)");
            boolean t11 = badResp.statusCode() == 400 && badResp.body().contains("\"VALIDATION_ERROR\"");

            allPassed = t1 && t2 && t3 && t4 && t5 && t6 && t7 && t8 && t9 && t10 && t11;

            System.out.println();
            System.out.println("========================================");
            System.out.println("REST API END-TO-END: " + (allPassed ? "PASSED" : "FAILED"));
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("API E2E Test error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            server.stop();
            System.out.println("Embedded REST API Server stopped.");
            System.exit(allPassed ? 0 : 1);
        }
    }
}
