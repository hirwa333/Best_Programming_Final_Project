package com.impano.logistics.ui;

import com.impano.logistics.model.*;
import com.impano.logistics.repository.*;
import com.impano.logistics.service.*;
import com.impano.logistics.repository.DataStore;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * ConsoleApp is the main UI layer of the SLTMS.
 * It handles the full user journey: Login → Dashboard → Operations → Logout.
 * All input/output is done via the console (System.in / System.out).
 */
public class ConsoleApp {

    // ---- Services (business logic layer) ----
    private final ClientService clientService;
    private final DriverService driverService;
    private final VehicleService vehicleService;
    private final ShipmentService shipmentService;
    private final InvoiceService invoiceService;
    private final AuthService authService;

    private final Scanner scanner = new Scanner(System.in);

    // Keep references to repos so we can save after every change
    private final ClientRepository  clientRepo;
    private final DriverRepository  driverRepo;
    private final VehicleRepository vehicleRepo;

    public ConsoleApp() {
        // Initialize all repositories (in-memory data store)
        this.clientRepo  = new ClientRepository();
        this.driverRepo  = new DriverRepository();
        this.vehicleRepo = new VehicleRepository();
        ShipmentRepository shipmentRepo = new ShipmentRepository();
        InvoiceRepository  invoiceRepo  = new InvoiceRepository();

        // Wire services with their repositories
        this.clientService   = new ClientService(clientRepo);
        this.driverService   = new DriverService(driverRepo);
        this.vehicleService  = new VehicleService(vehicleRepo);
        this.shipmentService = new ShipmentService(shipmentRepo, driverRepo, vehicleRepo, invoiceRepo);
        this.invoiceService  = new InvoiceService(invoiceRepo);
        this.authService     = new AuthService();

        // Load saved data first, then seed only if nothing was loaded
        DataStore.load(clientRepo, driverRepo, vehicleRepo);
        if (driverRepo.findAll().isEmpty())  seedData(driverRepo, vehicleRepo);
        seedUsers();
    }

    /**
     * Seeds default drivers and vehicles into the system.
     * This simulates pre-existing company fleet and staff data.
     */
    private void seedData(DriverRepository driverRepo, VehicleRepository vehicleRepo) {
        driverRepo.save(new Driver("DRV-001", "Jean Bosco",   "bosco@impano.rw",   "pass123", "RW-DL-001"));
        driverRepo.save(new Driver("DRV-002", "Amina Uwase",  "amina@impano.rw",   "pass123", "RW-DL-002"));
        vehicleRepo.save(new Vehicle("VEH-001", "RAC 001A", VehicleType.TRUCK,      5000));
        vehicleRepo.save(new Vehicle("VEH-002", "RAB 202B", VehicleType.VAN,        1500));
        vehicleRepo.save(new Vehicle("VEH-003", "RAD 303C", VehicleType.MOTORCYCLE, 100));
        // Pre-registered client
        clientRepo.save(new Client("CLT-001", "Nahimana", "nahimana@impano.rw", "pass123", "+250788000001", "Kigali, Rwanda"));
    }

    /**
     * Seeds default admin and manager accounts for login.
     * In production, these would be stored in a database.
     */
    private void seedUsers() {
        authService.registerUser(new Admin("ADM-001", "Hirwa Roy", "admin@impano.rw", "admin123"));
        authService.registerUser(new Admin("ADM-002", "Manager One", "manager@impano.rw", "manager123"));
    }

    /**
     * Main entry point of the application.
     * Implements the full user journey: Login → Dashboard → Logout.
     */
    public void run() {
        printBanner();

        // Keep showing login screen until user successfully logs in or exits
        boolean appRunning = true;
        while (appRunning) {
            showLoginScreen();

            // If login was successful, show the dashboard
            if (authService.isLoggedIn()) {
                showDashboard();
            }

            // After logout, ask if user wants to log in again or exit
            System.out.println("\n1. Login again");
            System.out.println("0. Exit application");
            int choice = readInt("Choice: ");
            if (choice == 0) {
                appRunning = false;
                System.out.println("\nThank you for using SLTMS. Goodbye!");
                System.out.println("Impano Gateway Logistics Ltd");
            }
        }
    }

    /**
     * Displays the login screen and handles authentication.
     * User must enter valid email and password to proceed.
     * Simulates: Input Processing — login form with validation.
     */
    private void showLoginScreen() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           SYSTEM LOGIN               ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("  Default: admin@impano.rw / admin123");
        System.out.println("----------------------------------------");

        int attempts = 0;
        // Allow up to 3 login attempts before locking out
        while (attempts < 3) {
            String email = readString("Email: ");
            String password = readString("Password: ");

            Optional<User> user = authService.login(email, password);

            if (user.isPresent()) {
                // Login successful — show welcome message
                System.out.println("\n✔ Login successful! Welcome, " + user.get().getName());
                System.out.println("  Role: " + user.get().getRole());
                return;
            } else {
                attempts++;
                System.out.println("✘ Invalid email or password. Attempts left: " + (3 - attempts));
            }
        }
        System.out.println("Too many failed attempts. Access denied.");
    }

    /**
     * Displays the main dashboard after successful login.
     * This is the central hub — user navigates all modules from here.
     * Simulates: User journey — login → dashboard → module → logout.
     */
    private void showDashboard() {
        boolean sessionActive = true;

        while (sessionActive) {
            // Show who is logged in at the top of every menu
            User currentUser = authService.getCurrentUser().get();
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║         SLTMS — MAIN DASHBOARD               ║");
            System.out.println("║  Logged in as: " + padRight(currentUser.getName(), 28) + "║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.println("  1. Manage Clients");
            System.out.println("  2. Manage Drivers");
            System.out.println("  3. Manage Vehicles");
            System.out.println("  4. Manage Shipments");
            System.out.println("  5. Manage Invoices");
            System.out.println("  6. Generate Report");
            System.out.println("  0. Logout");
            System.out.println("------------------------------------------------");

            int choice = readInt("Select option: ");

            switch (choice) {
                case 1 -> manageClients();
                case 2 -> manageDrivers();
                case 3 -> manageVehicles();
                case 4 -> manageShipments();
                case 5 -> manageInvoices();
                case 6 -> generateReport();
                case 0 -> {
                    // Logout — clear session and return to login screen
                    System.out.println("\n✔ Logged out successfully. Session ended.");
                    authService.logout();
                    sessionActive = false;
                }
                default -> System.out.println("Invalid option. Please choose from the menu.");
            }
        }
    }

    // ---- CLIENT MANAGEMENT ----
    /**
     * Handles client registration and listing.
     * Validates duplicate emails before saving.
     */
    private void manageClients() {
        System.out.println("\n--- CLIENT MANAGEMENT ---");
        System.out.println("1. Register New Client");
        System.out.println("2. List All Clients");
        System.out.println("0. Back to Dashboard");
        int choice = readInt("Choice: ");

        if (choice == 1) {
            // Collect client details from user input
            String name = readString("Full Name: ");
            String email = readString("Email: ");
            String password = readString("Password: ");
            String phone = readString("Phone: ");
            String address = readString("Address: ");
            try {
                Client c = clientService.registerClient(name, email, password, phone, address);
                DataStore.save(clientRepo, driverRepo, vehicleRepo);
                System.out.println("✔ Client registered successfully!");
                System.out.println("  " + c);
            } catch (IllegalArgumentException e) {
                // Duplicate email — fail fast with clear message
                System.out.println("✘ Error: " + e.getMessage());
            }
        } else if (choice == 2) {
            List<Client> clients = clientService.getAllClients();
            if (clients.isEmpty()) {
                System.out.println("No clients registered yet.");
            } else {
                System.out.println("\n--- ALL CLIENTS (" + clients.size() + ") ---");
                clients.forEach(c -> System.out.println("  " + c));
            }
        }
    }

    // ---- DRIVER MANAGEMENT ----
    /**
     * Handles driver registration and listing.
     */
    private void manageDrivers() {
        System.out.println("\n--- DRIVER MANAGEMENT ---");
        System.out.println("1. Register New Driver");
        System.out.println("2. List All Drivers");
        System.out.println("0. Back to Dashboard");
        int choice = readInt("Choice: ");

        if (choice == 1) {
            String name = readString("Full Name: ");
            String email = readString("Email: ");
            String password = readString("Password: ");
            String license = readString("License Number: ");
            Driver d = driverService.registerDriver(name, email, password, license);
            DataStore.save(clientRepo, driverRepo, vehicleRepo);
            System.out.println("✔ Driver registered: " + d);
        } else if (choice == 2) {
            List<Driver> drivers = driverService.getAllDrivers();
            if (drivers.isEmpty()) {
                System.out.println("No drivers registered yet.");
            } else {
                System.out.println("\n--- ALL DRIVERS (" + drivers.size() + ") ---");
                drivers.forEach(d -> System.out.println("  " + d));
            }
        }
    }

    // ---- VEHICLE MANAGEMENT ----
    /**
     * Handles adding vehicles to the fleet and listing them.
     */
    private void manageVehicles() {
        System.out.println("\n--- VEHICLE MANAGEMENT ---");
        System.out.println("1. Add Vehicle to Fleet");
        System.out.println("2. List All Vehicles");
        System.out.println("0. Back to Dashboard");
        int choice = readInt("Choice: ");

        if (choice == 1) {
            String plate = readString("Plate Number: ");
            System.out.println("Vehicle Type options: TRUCK / VAN / MOTORCYCLE / PICKUP");
            VehicleType type = VehicleType.valueOf(readString("Type: ").toUpperCase());
            double capacity = readDouble("Capacity (kg): ");
            Vehicle v = vehicleService.addVehicle(plate, type, capacity);
            DataStore.save(clientRepo, driverRepo, vehicleRepo);
            System.out.println("✔ Vehicle added: " + v);
        } else if (choice == 2) {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            if (vehicles.isEmpty()) {
                System.out.println("No vehicles in fleet yet.");
            } else {
                System.out.println("\n--- FLEET (" + vehicles.size() + " vehicles) ---");
                vehicles.forEach(v -> System.out.println("  " + v));
            }
        }
    }

    // ---- SHIPMENT MANAGEMENT ----
    /**
     * Central shipment hub — register, track, update, and list shipments.
     */
    private void manageShipments() {
        System.out.println("\n--- SHIPMENT MANAGEMENT ---");
        System.out.println("1. Register New Shipment");
        System.out.println("2. Update Shipment Status");
        System.out.println("3. Track Shipment by ID");
        System.out.println("4. List All Shipments");
        System.out.println("0. Back to Dashboard");
        int choice = readInt("Choice: ");

        switch (choice) {
            case 1 -> registerShipment();
            case 2 -> updateShipmentStatus();
            case 3 -> trackShipment();
            case 4 -> {
                List<Shipment> shipments = shipmentService.getAllShipments();
                if (shipments.isEmpty()) {
                    System.out.println("No shipments found.");
                } else {
                    System.out.println("\n--- ALL SHIPMENTS (" + shipments.size() + ") ---");
                    shipments.forEach(s -> System.out.println("  " + s));
                }
            }
        }
    }

    /**
     * Registers a new shipment for a client.
     * Automatically assigns an available driver and vehicle.
     * Uses Factory Pattern to create the shipment and Strategy Pattern to calculate cost.
     */
    private void registerShipment() {
        System.out.println("\n-- Register New Shipment --");

        // Look up the client first — shipment cannot exist without a client
        String clientId = readString("Client ID: ");
        Optional<Client> client = clientService.findById(clientId);
        if (client.isEmpty()) {
            System.out.println("✘ Client not found. Please register the client first.");
            return;
        }

        String origin = readString("Origin city: ");
        String destination = readString("Destination city: ");
        double weight = readDouble("Cargo weight (kg): ");
        double distance = readDouble("Distance (km): ");

        // ShipmentService handles assignment and cost calculation internally
        Shipment shipment = shipmentService.registerShipment(origin, destination, weight, distance, client.get());

        System.out.println("\n✔ Shipment created successfully!");
        System.out.println("  Shipment ID : " + shipment.getShipmentId());
        System.out.println("  Status      : " + shipment.getStatus());

        if (shipment.getStatus() == ShipmentStatus.ASSIGNED) {
            System.out.println("  Driver      : " + shipment.getDriver().getName());
            System.out.println("  Vehicle     : " + shipment.getVehicle().getPlateNumber());
            System.out.printf("  Cost        : %.2f RWF%n", shipment.getCost());
            System.out.println("  Invoice     : Auto-generated ✔");
        } else {
            System.out.println("  ⚠ No available driver/vehicle. Shipment queued as PENDING.");
        }
    }

    /**
     * Updates the delivery status of an existing shipment.
     * When status is DELIVERED or CANCELLED, driver and vehicle are freed automatically.
     */
    private void updateShipmentStatus() {
        String id = readString("Shipment ID: ");
        System.out.println("Status options: PENDING / ASSIGNED / IN_TRANSIT / DELIVERED / CANCELLED");
        ShipmentStatus status = ShipmentStatus.valueOf(readString("New Status: ").toUpperCase());
        boolean updated = shipmentService.updateStatus(id, status);
        System.out.println(updated ? "✔ Status updated to " + status : "✘ Shipment not found.");
    }

    /**
     * Displays full details of a shipment by its ID.
     * This is the shipment tracking feature for clients and managers.
     */
    private void trackShipment() {
        String id = readString("Shipment ID: ");
        Optional<Shipment> opt = shipmentService.findById(id);
        if (opt.isEmpty()) {
            System.out.println("✘ Shipment not found.");
            return;
        }
        Shipment s = opt.get();
        System.out.println("\n--- SHIPMENT TRACKING ---");
        System.out.println("  ID          : " + s.getShipmentId());
        System.out.println("  From        : " + s.getOrigin());
        System.out.println("  To          : " + s.getDestination());
        System.out.println("  Weight      : " + s.getWeightKg() + " kg");
        System.out.println("  Distance    : " + s.getDistanceKm() + " km");
        System.out.println("  Status      : " + s.getStatus());
        System.out.printf("  Cost        : %.2f RWF%n", s.getCost());
        System.out.println("  Client      : " + s.getClient().getName());
        if (s.getDriver() != null)  System.out.println("  Driver      : " + s.getDriver().getName());
        if (s.getVehicle() != null) System.out.println("  Vehicle     : " + s.getVehicle().getPlateNumber());
        System.out.println("  Created     : " + s.getCreatedAt());
    }

    // ---- INVOICE MANAGEMENT ----
    /**
     * Handles invoice listing, payment, and lookup by shipment.
     */
    private void manageInvoices() {
        System.out.println("\n--- INVOICE MANAGEMENT ---");
        System.out.println("1. List All Invoices");
        System.out.println("2. Pay Invoice");
        System.out.println("3. Find Invoice by Shipment ID");
        System.out.println("0. Back to Dashboard");
        int choice = readInt("Choice: ");

        switch (choice) {
            case 1 -> {
                List<Invoice> invoices = invoiceService.getAllInvoices();
                if (invoices.isEmpty()) {
                    System.out.println("No invoices found.");
                } else {
                    System.out.println("\n--- ALL INVOICES (" + invoices.size() + ") ---");
                    invoices.forEach(i -> System.out.println("  " + i));
                }
            }
            case 2 -> {
                String id = readString("Invoice ID: ");
                boolean paid = invoiceService.payInvoice(id);
                System.out.println(paid ? "✔ Invoice marked as PAID." : "✘ Invoice not found.");
            }
            case 3 -> {
                String shipmentId = readString("Shipment ID: ");
                invoiceService.findByShipmentId(shipmentId)
                        .ifPresentOrElse(
                                i -> System.out.println("  " + i),
                                () -> System.out.println("✘ No invoice found for this shipment."));
            }
        }
    }

    // ---- REPORT ----
    /**
     * Generates a summary report of all system data.
     * Shows totals for clients, drivers, vehicles, shipments, and revenue.
     */
    private void generateReport() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         SYSTEM REPORT                ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("  Total Clients   : " + clientService.getAllClients().size());
        System.out.println("  Total Drivers   : " + driverService.getAllDrivers().size());
        System.out.println("  Total Vehicles  : " + vehicleService.getAllVehicles().size());
        System.out.println("  Total Shipments : " + shipmentService.getAllShipments().size());

        // Calculate total revenue from paid invoices only
        double totalRevenue = invoiceService.getAllInvoices().stream()
                .filter(Invoice::isPaid)
                .mapToDouble(Invoice::getAmount)
                .sum();
        System.out.printf("  Total Revenue   : %.2f RWF%n", totalRevenue);

        // Count shipments by status for operational overview
        long pending   = shipmentService.getAllShipments().stream().filter(s -> s.getStatus() == ShipmentStatus.PENDING).count();
        long inTransit = shipmentService.getAllShipments().stream().filter(s -> s.getStatus() == ShipmentStatus.IN_TRANSIT).count();
        long delivered = shipmentService.getAllShipments().stream().filter(s -> s.getStatus() == ShipmentStatus.DELIVERED).count();

        System.out.println("  Pending         : " + pending);
        System.out.println("  In Transit      : " + inTransit);
        System.out.println("  Delivered       : " + delivered);
        System.out.println("════════════════════════════════════════");
    }

    // ---- BANNER ----
    private void printBanner() {
        System.out.println("╔═════════════════════════════════════════════════════╗");
        System.out.println("║       IMPANO GATEWAY LOGISTICS LTD                  ║");
        System.out.println("║  Smart Logistics & Transportation Management System ║");
        System.out.println("║                    SLTMS v1.0                       ║");
        System.out.println("╚═════════════════════════════════════════════════════╝");
    }

    // ---- INPUT HELPERS ----
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /** Pads a string to a fixed width for aligned console output */
    private String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}
