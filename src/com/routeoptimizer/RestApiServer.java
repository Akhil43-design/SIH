package com.routeoptimizer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.Executors;

public class RestApiServer {

    private final ServerConfiguration config;
    private final DatabaseManager databaseManager;
    private final FleetManagementService fleetService;
    private final TrafficService trafficService;
    private final OptimizationService optimizationService;

    private final HealthController healthController;
    private final CustomerController customerController;
    private final VehicleController vehicleController;
    private final DepotController depotController;
    private final TrafficController trafficController;
    private final OptimizationController optimizationController;

    private HttpServer server;

    public RestApiServer(ServerConfiguration config, DatabaseManager db) {
        this.config = config != null ? config : new ServerConfiguration();
        this.databaseManager = db != null ? db : new DatabaseManager();

        this.fleetService = new FleetManagementService(this.databaseManager);
        this.trafficService = new TrafficService(this.databaseManager);
        this.optimizationService = new OptimizationService(this.fleetService, this.trafficService, this.databaseManager);

        this.healthController = new HealthController(this.config);
        this.customerController = new CustomerController(this.fleetService);
        this.vehicleController = new VehicleController(this.fleetService);
        this.depotController = new DepotController(this.fleetService);
        this.trafficController = new TrafficController(this.trafficService, this.fleetService);
        this.optimizationController = new OptimizationController(this.optimizationService);
    }

    public RestApiServer(ServerConfiguration config) {
        this(config, new DatabaseManager());
    }

    public RestApiServer(int port) {
        this(new ServerConfiguration(port, "*", "SYNTHETIC", "SIMULATED"), new DatabaseManager());
    }

    public RestApiServer() {
        this(new ServerConfiguration(), new DatabaseManager());
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
        server.createContext("/api/v1/", new ApiDispatcher());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public int getPort() {
        if (server != null) {
            return server.getAddress().getPort();
        }
        return config.getPort();
    }

    private class ApiDispatcher implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            // Set CORS Headers
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", config.getAllowedOrigins());
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                String body = readRequestBody(exchange);
                String responseJson = "";
                int statusCode = 200;

                if (path.equals("/api/v1/health") && "GET".equalsIgnoreCase(method)) {
                    responseJson = JsonUtils.toJson(healthController.getHealth());
                } else if (path.equals("/api/v1/customers")) {
                    if ("GET".equalsIgnoreCase(method)) {
                        responseJson = JsonUtils.toJson(customerController.getAllCustomers());
                    } else if ("POST".equalsIgnoreCase(method)) {
                        CustomerDto dto = JsonUtils.parseCustomerDto(body);
                        responseJson = JsonUtils.toJson(customerController.createCustomer(dto));
                        statusCode = 201;
                    } else {
                        throw new ApiException(405, "METHOD_NOT_ALLOWED", "Method " + method + " not supported.");
                    }
                } else if (path.startsWith("/api/v1/customers/")) {
                    String id = path.substring("/api/v1/customers/".length());
                    if ("GET".equalsIgnoreCase(method)) {
                        responseJson = JsonUtils.toJson(customerController.getCustomer(id));
                    } else if ("PUT".equalsIgnoreCase(method)) {
                        CustomerDto dto = JsonUtils.parseCustomerDto(body);
                        responseJson = JsonUtils.toJson(customerController.updateCustomer(id, dto));
                    } else if ("DELETE".equalsIgnoreCase(method)) {
                        customerController.deleteCustomer(id);
                        statusCode = 204;
                    } else {
                        throw new ApiException(405, "METHOD_NOT_ALLOWED", "Method " + method + " not supported.");
                    }
                } else if (path.equals("/api/v1/vehicles")) {
                    if ("GET".equalsIgnoreCase(method)) {
                        responseJson = JsonUtils.toJson(vehicleController.getAllVehicles());
                    } else if ("POST".equalsIgnoreCase(method)) {
                        VehicleDto dto = JsonUtils.parseVehicleDto(body);
                        responseJson = JsonUtils.toJson(vehicleController.createVehicle(dto));
                        statusCode = 201;
                    } else {
                        throw new ApiException(405, "METHOD_NOT_ALLOWED", "Method " + method + " not supported.");
                    }
                } else if (path.startsWith("/api/v1/vehicles/")) {
                    String id = path.substring("/api/v1/vehicles/".length());
                    if ("GET".equalsIgnoreCase(method)) {
                        responseJson = JsonUtils.toJson(vehicleController.getVehicle(id));
                    } else if ("PUT".equalsIgnoreCase(method)) {
                        VehicleDto dto = JsonUtils.parseVehicleDto(body);
                        responseJson = JsonUtils.toJson(vehicleController.updateVehicle(id, dto));
                    } else if ("DELETE".equalsIgnoreCase(method)) {
                        vehicleController.deleteVehicle(id);
                        statusCode = 204;
                    } else {
                        throw new ApiException(405, "METHOD_NOT_ALLOWED", "Method " + method + " not supported.");
                    }
                } else if (path.equals("/api/v1/depots")) {
                    if ("GET".equalsIgnoreCase(method)) {
                        responseJson = JsonUtils.toJson(depotController.getAllDepots());
                    } else if ("POST".equalsIgnoreCase(method)) {
                        DepotDto dto = JsonUtils.parseDepotDto(body);
                        responseJson = JsonUtils.toJson(depotController.createDepot(dto));
                        statusCode = 201;
                    } else {
                        throw new ApiException(405, "METHOD_NOT_ALLOWED", "Method " + method + " not supported.");
                    }
                } else if (path.startsWith("/api/v1/depots/")) {
                    String id = path.substring("/api/v1/depots/".length());
                    if ("GET".equalsIgnoreCase(method)) {
                        responseJson = JsonUtils.toJson(depotController.getDepot(id));
                    } else if ("PUT".equalsIgnoreCase(method)) {
                        DepotDto dto = JsonUtils.parseDepotDto(body);
                        responseJson = JsonUtils.toJson(depotController.updateDepot(id, dto));
                    } else if ("DELETE".equalsIgnoreCase(method)) {
                        depotController.deleteDepot(id);
                        statusCode = 204;
                    } else {
                        throw new ApiException(405, "METHOD_NOT_ALLOWED", "Method " + method + " not supported.");
                    }
                } else if (path.equals("/api/v1/traffic/update") && "POST".equalsIgnoreCase(method)) {
                    TrafficUpdateRequest req = JsonUtils.parseTrafficUpdateRequest(body);
                    TrafficUpdate tu = trafficController.updateTraffic(req);
                    responseJson = String.format(Locale.US,
                            "{\"status\":\"UPDATED\",\"originId\":\"%s\",\"destinationId\":\"%s\",\"newMultiplier\":%.2f}",
                            JsonUtils.escape(tu.getOrigin().getId()), JsonUtils.escape(tu.getDestination().getId()), tu.getNewMultiplier());
                } else if (path.equals("/api/v1/optimization") && "GET".equalsIgnoreCase(method)) {
                    responseJson = JsonUtils.toJson(optimizationController.getOptimizationHistory(null, 50));
                } else if (path.equals("/api/v1/optimization/run") && "POST".equalsIgnoreCase(method)) {
                    OptimizationRequest req = JsonUtils.parseOptimizationRequest(body);
                    OptimizationResponse resp = optimizationController.runOptimization(req);
                    responseJson = JsonUtils.toJson(resp);
                } else if (path.startsWith("/api/v1/optimization/") && path.endsWith("/reoptimize") && "POST".equalsIgnoreCase(method)) {
                    String id = path.substring("/api/v1/optimization/".length(), path.length() - "/reoptimize".length());
                    TrafficUpdateRequest req = JsonUtils.parseTrafficUpdateRequest(body);
                    OptimizationResponse resp = optimizationController.reoptimize(id, req);
                    responseJson = JsonUtils.toJson(resp);
                } else if (path.startsWith("/api/v1/optimization/") && "GET".equalsIgnoreCase(method)) {
                    String id = path.substring("/api/v1/optimization/".length());
                    OptimizationResponse resp = optimizationController.getOptimization(id);
                    responseJson = JsonUtils.toJson(resp);
                } else {
                    throw new ResourceNotFoundException("Endpoint not found: " + path);
                }

                sendJsonResponse(exchange, statusCode, responseJson);

            } catch (ApiException e) {
                ApiErrorResponse err = new ApiErrorResponse(e.getStatusCode(), e.getErrorCode(), e.getMessage(), path);
                sendJsonResponse(exchange, e.getStatusCode(), JsonUtils.toJson(err));
            } catch (Exception e) {
                ApiErrorResponse err = new ApiErrorResponse(500, "INTERNAL_SERVER_ERROR", e.getMessage(), path);
                sendJsonResponse(exchange, 500, JsonUtils.toJson(err));
            }
        }

        private String readRequestBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }

        private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
            if (statusCode == 204 || bytes.length == 0) {
                exchange.sendResponseHeaders(statusCode, -1);
            } else {
                exchange.sendResponseHeaders(statusCode, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.flush();
                os.close();
            }
        }
    }

    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public FleetManagementService getFleetService() { return fleetService; }
    public TrafficService getTrafficService() { return trafficService; }
    public OptimizationService getOptimizationService() { return optimizationService; }
    public CustomerController getCustomerController() { return customerController; }
    public VehicleController getVehicleController() { return vehicleController; }
    public DepotController getDepotController() { return depotController; }
    public TrafficController getTrafficController() { return trafficController; }
    public OptimizationController getOptimizationController() { return optimizationController; }
    public HealthController getHealthController() { return healthController; }

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            ServerConfiguration config = new ServerConfiguration(port, "*", "REAL_OSRM", "SIMULATED");
            DatabaseManager db = new DatabaseManager();
            RestApiServer server = new RestApiServer(config, db);

            // Pre-seed sample Bengaluru Indian multi-depot fleet if database is fresh
            if (server.getFleetService().getDepotRepo().findAll().isEmpty()) {
                System.out.println("Initializing sample Bengaluru Indian multi-depot fleet...");
                server.getDepotController().createDepot(new DepotDto("W1", "Peenya Industrial Area, Bengaluru", 12.9978, 77.5587));
                server.getDepotController().createDepot(new DepotDto("W2", "Hosur Road Logistics Hub, Bengaluru", 12.8769, 77.6308));

                server.getVehicleController().createVehicle(new VehicleDto("V1", 80.0, "W1", 0.12, 10.0));
                server.getVehicleController().createVehicle(new VehicleDto("V2", 80.0, "W1", 0.12, 10.0));
                server.getVehicleController().createVehicle(new VehicleDto("V3", 90.0, "W2", 0.12, 10.0));

                server.getCustomerController().createCustomer(new CustomerDto("C1", "Manyata Tech Park, Nagavara", 13.0475, 77.6200, 20.0, "HIGH", 5.0, 30.0, 180.0));
                server.getCustomerController().createCustomer(new CustomerDto("C2", "Phoenix Marketcity, Whitefield", 12.9959, 77.6964, 15.0, "MEDIUM", 5.0, 30.0, 240.0));
                server.getCustomerController().createCustomer(new CustomerDto("C3", "Rajajinagar Industrial Area", 12.9880, 77.5540, 20.0, "HIGH", 5.0, 60.0, 300.0));
                server.getCustomerController().createCustomer(new CustomerDto("C4", "Koramangala Commercial Hub", 12.9352, 77.6245, 25.0, "MEDIUM", 5.0, 45.0, 240.0));
                server.getCustomerController().createCustomer(new CustomerDto("C5", "Electronic City Phase 1", 12.8452, 77.6602, 30.0, "LOW", 5.0, 60.0, 360.0));
                server.getCustomerController().createCustomer(new CustomerDto("C6", "Indiranagar 100 Feet Road", 12.9784, 77.6408, 15.0, "HIGH", 5.0, 30.0, 180.0));
                server.getCustomerController().createCustomer(new CustomerDto("C7", "Jayanagar 4th Block", 12.9299, 77.5824, 35.0, "MEDIUM", 5.0, 90.0, 420.0));
                server.getCustomerController().createCustomer(new CustomerDto("C8", "Marathahalli Junction", 12.9591, 77.6974, 20.0, "LOW", 5.0, 60.0, 360.0));
            }

            server.start();
            System.out.println("=================================================");
            System.out.println("⚡ QuantumRouteOptimizer REST API Backend Online");
            System.out.println("   Local URL: http://localhost:" + port);
            System.out.println("   Health:    http://localhost:" + port + "/api/v1/health");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
