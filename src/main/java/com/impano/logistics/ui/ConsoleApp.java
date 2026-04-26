package com.impano.logistics.ui;

import com.impano.logistics.model.*;
import com.impano.logistics.repository.*;
import com.impano.logistics.service.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based UI for Smart Logistics & Transportation Management System
 * Impano Gateway Logistics Ltd
 */
public class ConsoleApp {

    private final ClientService clientService;
    private final DriverService driverService;
    private final VehicleService vehicleService;
    private final ShipmentService shipmentService;
    private final InvoiceService invoiceService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp() {
        ClientRepository clientRepo = new ClientRepository();
        DriverRepository driverRepo = new DriverRepository();
        VehicleRepository vehicleRepo = new VehicleRepository();
        ShipmentRepository shipmentRepo = new ShipmentRepository();
        InvoiceRepository invoiceRepo = new InvoiceRepository();

        this.clientService = new ClientService(clientRepo);
        this.driverService = new DriverService(driverRepo);
        this.vehicleService = new VehicleService(vehicleRepo);
        this.shipmentService = new ShipmentService(shipmentRepo, driverRepo, vehicleRepo, invoiceRepo);
        this.invoiceService = new InvoiceService(invoiceRepo);

        seedData(driverRepo, vehicleRepo);
    }

    private void seedData(DriverRepository driverRepo, VehicleRepository vehicleRepo) {
        driverRepo.save(new Driver("DRV-001", "Jean Bosco", "bosco@impano.rw", "pass123", "RW-DL-001"));
        driverRepo.save(new Driver("DRV-002", "Amina Uwase", "amina@impano.rw", "pass123", "RW-DL-002"));
        vehicleRepo.save(new Vehicle("VEH-001", "RAC 001A", VehicleType.TRUCK, 5000));
        vehicleRepo.save(new Vehicle("VEH-002", "RAB 202B", VehicleType.VAN, 1500));
        vehicleRepo.save(new Vehicle("VEH-003", "RAD 303C", VehicleType.MOTORCYCLE, 100));
    }

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1 -> manageClients();
                case 2 -> manageDrivers();
                case 3 -> manageVehicles();
                case 4 -> manageShipments();
                case 5 -> manageInvoices();
                case 6 -> generateReport();
                case 0 -> { running = false; System.out.println("\nGoodbye! — Impano Gateway Logistics Ltd"); }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void printBanner() {
        System.out.println("=============================================================");
        System.out.println("   IMPANO GATEWAY LOGISTICS LTD");
        System.out.println("   Smart Logistics & Transportation Management System");
        System.out.println("=============================================================");
    }

    private void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Manage Clients");
        System.out.println("2. Manage Drivers");
        System.out.println("3. Manage Vehicles");
        System.out.println("4. Manage Shipments");
        System.out.println("5. Manage Invoices");
        System.out.println("6. Generate Report");
        System.out.println("0. Exit");
    }

    // ---- CLIENT MANAGEMENT ----
    private void manageClients() {
        System.out.println("\n--- CLIENT MANAGEMENT ---");
        System.out.println("1. Register Client");
        System.out.println("2. List All Clients");
        int choice = readInt("Choice: ");
        if (choice == 1) {
            String name = readString("Name: ");
            String email = readString("Email: ");
            String password = readString("Password: ");
            String phone = readString("Phone: ");
            String address = readString("Address: ");
            try {
                Client c = clientService.registerClient(name, email, password, phone, address);
                System.out.println("Client registered: " + c);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else if (choice == 2) {
            List<Client> clients = clientService.getAllClients();
            if (clients.isEmpty()) System.out.println("No clients registered.");
            else clients.forEach(System.out::println);
        }
    }

    // ---- DRIVER MANAGEMENT ----
    private void manageDrivers() {
        System.out.println("\n--- DRIVER MANAGEMENT ---");
        System.out.println("1. Register Driver");
        System.out.println("2. List All Drivers");
        int choice = readInt("Choice: ");
        if (choice == 1) {
            String name = readString("Name: ");
            String email = readString("Email: ");
            String password = readString("Password: ");
            String license = readString("License Number: ");
            Driver d = driverService.registerDriver(name, email, password, license);
            System.out.println("Driver registered: " + d);
        } else if (choice == 2) {
            List<Driver> drivers = driverService.getAllDrivers();
            if (drivers.isEmpty()) System.out.println("No drivers registered.");
            else drivers.forEach(System.out::println);
        }
    }

    // ---- VEHICLE MANAGEMENT ----
    private void manageVehicles() {
        System.out.println("\n--- VEHICLE MANAGEMENT ---");
        System.out.println("1. Add Vehicle");
        System.out.println("2. List All Vehicles");
        int choice = readInt("Choice: ");
        if (choice == 1) {
            String plate = readString("Plate Number: ");
            System.out.println("Type (TRUCK/VAN/MOTORCYCLE/PICKUP): ");
            VehicleType type = VehicleType.valueOf(readString("").toUpperCase());
            double capacity = readDouble("Capacity (kg): ");
            Vehicle v = vehicleService.addVehicle(plate, type, capacity);
            System.out.println("Vehicle added: " + v);
        } else if (choice == 2) {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            if (vehicles.isEmpty()) System.out.println("No vehicles registered.");
            else vehicles.forEach(System.out::println);
        }
    }

    // ---- SHIPMENT MANAGEMENT ----
    private void manageShipments() {
        System.out.println("\n--- SHIPMENT MANAGEMENT ---");
        System.out.println("1. Register New Shipment");
        System.out.println("2. Update Shipment Status");
        System.out.println("3. Track Shipment");
        System.out.println("4. List All Shipments");
        int choice = readInt("Choice: ");
        switch (choice) {
            case 1 -> registerShipment();
            case 2 -> updateShipmentStatus();
            case 3 -> trackShipment();
            case 4 -> {
                List<Shipment> shipments = shipmentService.getAllShipments();
                if (shipments.isEmpty()) System.out.println("No shipments found.");
                else shipments.forEach(System.out::println);
            }
        }
    }

    private void registerShipment() {
        String clientId = readString("Client ID: ");
        Optional<Client> client = clientService.findById(clientId);
        if (client.isEmpty()) { System.out.println("Client not found."); return; }

        String origin = readString("Origin: ");
        String destination = readString("Destination: ");
        double weight = readDouble("Weight (kg): ");
        double distance = readDouble("Distance (km): ");

        Shipment shipment = shipmentService.registerShipment(origin, destination, weight, distance, client.get());
        System.out.println("\nShipment created: " + shipment);
        if (shipment.getStatus() == ShipmentStatus.ASSIGNED) {
            System.out.println("Driver assigned: " + shipment.getDriver().getName());
            System.out.println("Vehicle assigned: " + shipment.getVehicle().getPlateNumber());
            System.out.printf("Estimated cost: %.2f RWF%n", shipment.getCost());
        } else {
            System.out.println("No available driver/vehicle. Shipment is PENDING.");
        }
    }

    private void updateShipmentStatus() {
        String id = readString("Shipment ID: ");
        System.out.println("New Status (PENDING/ASSIGNED/IN_TRANSIT/DELIVERED/CANCELLED): ");
        ShipmentStatus status = ShipmentStatus.valueOf(readString("").toUpperCase());
        boolean updated = shipmentService.updateStatus(id, status);
        System.out.println(updated ? "Status updated successfully." : "Shipment not found.");
    }

    private void trackShipment() {
        String id = readString("Shipment ID: ");
        Optional<Shipment> shipment = shipmentService.findById(id);
        if (shipment.isEmpty()) { System.out.println("Shipment not found."); return; }
        Shipment s = shipment.get();
        System.out.println("\n--- SHIPMENT DETAILS ---");
        System.out.println("ID       : " + s.getShipmentId());
        System.out.println("From     : " + s.getOrigin());
        System.out.println("To       : " + s.getDestination());
        System.out.println("Weight   : " + s.getWeightKg() + " kg");
        System.out.println("Distance : " + s.getDistanceKm() + " km");
        System.out.println("Status   : " + s.getStatus());
        System.out.printf("Cost     : %.2f RWF%n", s.getCost());
        System.out.println("Client   : " + s.getClient().getName());
        if (s.getDriver() != null) System.out.println("Driver   : " + s.getDriver().getName());
        if (s.getVehicle() != null) System.out.println("Vehicle  : " + s.getVehicle().getPlateNumber());
    }

    // ---- INVOICE MANAGEMENT ----
    private void manageInvoices() {
        System.out.println("\n--- INVOICE MANAGEMENT ---");
        System.out.println("1. List All Invoices");
        System.out.println("2. Pay Invoice");
        System.out.println("3. View Invoice by Shipment ID");
        int choice = readInt("Choice: ");
        switch (choice) {
            case 1 -> {
                List<Invoice> invoices = invoiceService.getAllInvoices();
                if (invoices.isEmpty()) System.out.println("No invoices found.");
                else invoices.forEach(System.out::println);
            }
            case 2 -> {
                String id = readString("Invoice ID: ");
                boolean paid = invoiceService.payInvoice(id);
                System.out.println(paid ? "Invoice marked as paid." : "Invoice not found.");
            }
            case 3 -> {
                String shipmentId = readString("Shipment ID: ");
                invoiceService.findByShipmentId(shipmentId)
                        .ifPresentOrElse(System.out::println, () -> System.out.println("No invoice found."));
            }
        }
    }

    // ---- REPORT ----
    private void generateReport() {
        System.out.println("\n========== SYSTEM REPORT ==========");
        System.out.println("Total Clients  : " + clientService.getAllClients().size());
        System.out.println("Total Drivers  : " + driverService.getAllDrivers().size());
        System.out.println("Total Vehicles : " + vehicleService.getAllVehicles().size());
        System.out.println("Total Shipments: " + shipmentService.getAllShipments().size());

        double totalRevenue = invoiceService.getAllInvoices().stream()
                .filter(Invoice::isPaid)
                .mapToDouble(Invoice::getAmount)
                .sum();
        System.out.printf("Total Revenue  : %.2f RWF%n", totalRevenue);

        long pending = shipmentService.getAllShipments().stream()
                .filter(s -> s.getStatus() == ShipmentStatus.PENDING).count();
        long delivered = shipmentService.getAllShipments().stream()
                .filter(s -> s.getStatus() == ShipmentStatus.DELIVERED).count();
        System.out.println("Pending Shipments  : " + pending);
        System.out.println("Delivered Shipments: " + delivered);
        System.out.println("====================================");
    }

    // ---- HELPERS ----
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            return val;
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
}
