package com.impano.logistics.api;

import com.impano.logistics.model.*;
import com.impano.logistics.repository.*;
import com.impano.logistics.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ApiServer exposes the Java backend as a REST API on port 8080.
 * The frontend (HTML/JS) calls these endpoints to get real data.
 *
 * Endpoints:
 *   GET  /api/clients    — list all clients
 *   GET  /api/drivers    — list all drivers
 *   GET  /api/vehicles   — list all vehicles
 *   GET  /api/shipments  — list all shipments
 *   GET  /api/invoices   — list all invoices
 *   GET  /api/report     — summary report
 *   POST /api/login      — authenticate user
 */
public class ApiServer {

    private final ClientService   clientService;
    private final DriverService   driverService;
    private final VehicleService  vehicleService;
    private final ShipmentService shipmentService;
    private final InvoiceService  invoiceService;
    private final AuthService     authService;

    public ApiServer() {
        ClientRepository   clientRepo   = new ClientRepository();
        DriverRepository   driverRepo   = new DriverRepository();
        VehicleRepository  vehicleRepo  = new VehicleRepository();
        ShipmentRepository shipmentRepo = new ShipmentRepository();
        InvoiceRepository  invoiceRepo  = new InvoiceRepository();

        this.clientService   = new ClientService(clientRepo);
        this.driverService   = new DriverService(driverRepo);
        this.vehicleService  = new VehicleService(vehicleRepo);
        this.shipmentService = new ShipmentService(shipmentRepo, driverRepo, vehicleRepo, invoiceRepo);
        this.invoiceService  = new InvoiceService(invoiceRepo);
        this.authService     = new AuthService();

        seedData(driverRepo, vehicleRepo);
        seedUsers();
    }

    private void seedData(DriverRepository driverRepo, VehicleRepository vehicleRepo) {
        driverRepo.save(new Driver("DRV-001", "Jean Bosco",   "bosco@impano.rw",  "pass123", "RW-DL-001"));
        driverRepo.save(new Driver("DRV-002", "Amina Uwase",  "amina@impano.rw",  "pass123", "RW-DL-002"));
        driverRepo.save(new Driver("DRV-003", "Patrick Nkusi","patrick@impano.rw","pass123", "RW-DL-003"));
        vehicleRepo.save(new Vehicle("VEH-001", "RAC 001A", VehicleType.TRUCK,      5000));
        vehicleRepo.save(new Vehicle("VEH-002", "RAB 202B", VehicleType.VAN,        1500));
        vehicleRepo.save(new Vehicle("VEH-003", "RAD 303C", VehicleType.MOTORCYCLE, 100));
    }

    private void seedUsers() {
        authService.registerUser(new Admin("ADM-001", "Hirwa Roy",    "admin@impano.rw",   "admin123"));
        authService.registerUser(new Admin("ADM-002", "Manager One",  "manager@impano.rw", "manager123"));
    }

    /** Starts the HTTP server on port 8080 */
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Register all API routes
        server.createContext("/api/clients",   this::handleClients);
        server.createContext("/api/drivers",   this::handleDrivers);
        server.createContext("/api/vehicles",  this::handleVehicles);
        server.createContext("/api/shipments", this::handleShipments);
        server.createContext("/api/invoices",  this::handleInvoices);
        server.createContext("/api/report",    this::handleReport);
        server.createContext("/api/login",     this::handleLogin);

        server.setExecutor(null);
        server.start();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  SLTMS API Server started on port 8080   ║");
        System.out.println("║  Open frontend/index.html in browser     ║");
        System.out.println("║  API: http://localhost:8080/api/         ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ---- HANDLERS ----

    private void handleClients(HttpExchange ex) throws IOException {
        setCors(ex);
        if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
        StringBuilder sb = new StringBuilder("[");
        List<Client> list = clientService.getAllClients();
        for (int i = 0; i < list.size(); i++) {
            Client c = list.get(i);
            sb.append(String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"address\":\"%s\"}",
                c.getUserId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        sendJson(ex, sb.toString());
    }

    private void handleDrivers(HttpExchange ex) throws IOException {
        setCors(ex);
        if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
        StringBuilder sb = new StringBuilder("[");
        List<Driver> list = driverService.getAllDrivers();
        for (int i = 0; i < list.size(); i++) {
            Driver d = list.get(i);
            sb.append(String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"license\":\"%s\",\"available\":%b}",
                d.getUserId(), d.getName(), d.getEmail(), d.getLicenseNumber(), d.isAvailable()));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        sendJson(ex, sb.toString());
    }

    private void handleVehicles(HttpExchange ex) throws IOException {
        setCors(ex);
        if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
        StringBuilder sb = new StringBuilder("[");
        List<Vehicle> list = vehicleService.getAllVehicles();
        for (int i = 0; i < list.size(); i++) {
            Vehicle v = list.get(i);
            sb.append(String.format(
                "{\"id\":\"%s\",\"plate\":\"%s\",\"type\":\"%s\",\"capacity\":%.1f,\"status\":\"%s\"}",
                v.getVehicleId(), v.getPlateNumber(), v.getType(), v.getCapacityKg(), v.getStatus()));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        sendJson(ex, sb.toString());
    }

    private void handleShipments(HttpExchange ex) throws IOException {
        setCors(ex);
        if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
        StringBuilder sb = new StringBuilder("[");
        List<Shipment> list = shipmentService.getAllShipments();
        for (int i = 0; i < list.size(); i++) {
            Shipment s = list.get(i);
            String driver  = s.getDriver()  != null ? s.getDriver().getName()          : "";
            String vehicle = s.getVehicle() != null ? s.getVehicle().getPlateNumber()  : "";
            sb.append(String.format(
                "{\"id\":\"%s\",\"origin\":\"%s\",\"destination\":\"%s\",\"weight\":%.1f,\"distance\":%.1f,\"status\":\"%s\",\"client\":\"%s\",\"driver\":\"%s\",\"vehicle\":\"%s\",\"cost\":%.2f}",
                s.getShipmentId(), s.getOrigin(), s.getDestination(),
                s.getWeightKg(), s.getDistanceKm(), s.getStatus(),
                s.getClient().getName(), driver, vehicle, s.getCost()));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        sendJson(ex, sb.toString());
    }

    private void handleInvoices(HttpExchange ex) throws IOException {
        setCors(ex);
        if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
        StringBuilder sb = new StringBuilder("[");
        List<Invoice> list = invoiceService.getAllInvoices();
        for (int i = 0; i < list.size(); i++) {
            Invoice inv = list.get(i);
            sb.append(String.format(
                "{\"id\":\"%s\",\"shipmentId\":\"%s\",\"amount\":%.2f,\"paid\":%b}",
                inv.getInvoiceId(), inv.getShipment().getShipmentId(), inv.getAmount(), inv.isPaid()));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        sendJson(ex, sb.toString());
    }

    private void handleReport(HttpExchange ex) throws IOException {
        setCors(ex);
        if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
        double revenue = invoiceService.getAllInvoices().stream()
                .filter(Invoice::isPaid).mapToDouble(Invoice::getAmount).sum();
        long pending   = shipmentService.getAllShipments().stream().filter(s -> s.getStatus() == ShipmentStatus.PENDING).count();
        long delivered = shipmentService.getAllShipments().stream().filter(s -> s.getStatus() == ShipmentStatus.DELIVERED).count();
        String json = String.format(
            "{\"clients\":%d,\"drivers\":%d,\"vehicles\":%d,\"shipments\":%d,\"revenue\":%.2f,\"pending\":%d,\"delivered\":%d}",
            clientService.getAllClients().size(),
            driverService.getAllDrivers().size(),
            vehicleService.getAllVehicles().size(),
            shipmentService.getAllShipments().size(),
            revenue, pending, delivered);
        sendJson(ex, json);
    }

    private void handleLogin(HttpExchange ex) throws IOException {
        setCors(ex);
        if ("OPTIONS".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        // Simple parse: {"email":"...","password":"..."}
        String email    = extractJson(body, "email");
        String password = extractJson(body, "password");
        var user = authService.login(email, password);
        if (user.isPresent()) {
            String json = String.format("{\"success\":true,\"name\":\"%s\",\"role\":\"%s\"}",
                    user.get().getName(), user.get().getRole());
            sendJson(ex, json);
        } else {
            sendJson(ex, "{\"success\":false,\"message\":\"Invalid credentials\"}", 401);
        }
    }

    // ---- HELPERS ----

    private void setCors(HttpExchange ex) {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        ex.getResponseHeaders().add("Content-Type", "application/json");
    }

    private void sendJson(HttpExchange ex, String json) throws IOException {
        sendJson(ex, json, 200);
    }

    private void sendJson(HttpExchange ex, String json, int code) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    /** Simple JSON field extractor — no external library needed */
    private String extractJson(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }
}
