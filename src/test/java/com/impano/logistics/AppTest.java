package com.impano.logistics;

import com.impano.logistics.model.*;
import com.impano.logistics.repository.*;
import com.impano.logistics.service.*;

/**
 * Manual test runner for Smart Logistics & Transportation Management System
 * Run this class to verify core functionality without a test framework.
 */
public class AppTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testClientRegistration();
        testDuplicateClientEmail();
        testShipmentCreationWithAvailableResources();
        testShipmentStatusUpdate();
        testInvoiceGeneration();
        testCostCalculation();

        System.out.println("\n=== TEST RESULTS ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }

    static void testClientRegistration() {
        ClientService service = new ClientService(new ClientRepository());
        Client c = service.registerClient("Alice", "alice@test.com", "pass", "+250788000001", "Kigali");
        assert_(c != null, "testClientRegistration: client should not be null");
        assert_(c.getName().equals("Alice"), "testClientRegistration: name should be Alice");
        assert_(c.getUserId().startsWith("CLT-"), "testClientRegistration: ID should start with CLT-");
    }

    static void testDuplicateClientEmail() {
        ClientService service = new ClientService(new ClientRepository());
        service.registerClient("Bob", "bob@test.com", "pass", "+250788000002", "Musanze");
        try {
            service.registerClient("Bob2", "bob@test.com", "pass", "+250788000003", "Huye");
            fail("testDuplicateClientEmail: should have thrown exception");
        } catch (IllegalArgumentException e) {
            pass("testDuplicateClientEmail: duplicate email correctly rejected");
        }
    }

    static void testShipmentCreationWithAvailableResources() {
        ClientRepository clientRepo = new ClientRepository();
        DriverRepository driverRepo = new DriverRepository();
        VehicleRepository vehicleRepo = new VehicleRepository();
        ShipmentRepository shipmentRepo = new ShipmentRepository();
        InvoiceRepository invoiceRepo = new InvoiceRepository();

        Client client = new Client("CLT-T01", "Test Client", "tc@test.com", "pass", "+250788000004", "Kigali");
        clientRepo.save(client);
        driverRepo.save(new Driver("DRV-T01", "Test Driver", "td@test.com", "pass", "RW-DL-999"));
        vehicleRepo.save(new Vehicle("VEH-T01", "RAX 999Z", VehicleType.TRUCK, 3000));

        ShipmentService service = new ShipmentService(shipmentRepo, driverRepo, vehicleRepo, invoiceRepo);
        Shipment s = service.registerShipment("Kigali", "Butare", 200, 130, client);

        assert_(s != null, "testShipmentCreation: shipment should not be null");
        assert_(s.getStatus() == ShipmentStatus.ASSIGNED, "testShipmentCreation: status should be ASSIGNED");
        assert_(s.getDriver() != null, "testShipmentCreation: driver should be assigned");
        assert_(s.getVehicle() != null, "testShipmentCreation: vehicle should be assigned");
        assert_(s.getCost() > 0, "testShipmentCreation: cost should be greater than 0");
    }

    static void testShipmentStatusUpdate() {
        ClientRepository clientRepo = new ClientRepository();
        DriverRepository driverRepo = new DriverRepository();
        VehicleRepository vehicleRepo = new VehicleRepository();
        ShipmentRepository shipmentRepo = new ShipmentRepository();
        InvoiceRepository invoiceRepo = new InvoiceRepository();

        Client client = new Client("CLT-T02", "Status Client", "sc@test.com", "pass", "+250788000005", "Rubavu");
        driverRepo.save(new Driver("DRV-T02", "Status Driver", "sd@test.com", "pass", "RW-DL-888"));
        vehicleRepo.save(new Vehicle("VEH-T02", "RAY 888Y", VehicleType.VAN, 1000));

        ShipmentService service = new ShipmentService(shipmentRepo, driverRepo, vehicleRepo, invoiceRepo);
        Shipment s = service.registerShipment("Kigali", "Gisenyi", 50, 160, client);

        boolean updated = service.updateStatus(s.getShipmentId(), ShipmentStatus.IN_TRANSIT);
        assert_(updated, "testShipmentStatusUpdate: update should return true");
        assert_(s.getStatus() == ShipmentStatus.IN_TRANSIT, "testShipmentStatusUpdate: status should be IN_TRANSIT");
    }

    static void testInvoiceGeneration() {
        ClientRepository clientRepo = new ClientRepository();
        DriverRepository driverRepo = new DriverRepository();
        VehicleRepository vehicleRepo = new VehicleRepository();
        ShipmentRepository shipmentRepo = new ShipmentRepository();
        InvoiceRepository invoiceRepo = new InvoiceRepository();

        Client client = new Client("CLT-T03", "Invoice Client", "ic@test.com", "pass", "+250788000006", "Nyanza");
        driverRepo.save(new Driver("DRV-T03", "Invoice Driver", "id@test.com", "pass", "RW-DL-777"));
        vehicleRepo.save(new Vehicle("VEH-T03", "RAZ 777X", VehicleType.MOTORCYCLE, 80));

        ShipmentService shipmentService = new ShipmentService(shipmentRepo, driverRepo, vehicleRepo, invoiceRepo);
        InvoiceService invoiceService = new InvoiceService(invoiceRepo);

        Shipment s = shipmentService.registerShipment("Kigali", "Rwamagana", 10, 50, client);
        var invoice = invoiceService.findByShipmentId(s.getShipmentId());

        assert_(invoice.isPresent(), "testInvoiceGeneration: invoice should be generated");
        assert_(!invoice.get().isPaid(), "testInvoiceGeneration: invoice should not be paid yet");
    }

    static void testCostCalculation() {
        // Truck: 5000 + (100 * 150) + (200 * 20) = 5000 + 15000 + 4000 = 24000
        ClientRepository clientRepo = new ClientRepository();
        DriverRepository driverRepo = new DriverRepository();
        VehicleRepository vehicleRepo = new VehicleRepository();
        ShipmentRepository shipmentRepo = new ShipmentRepository();
        InvoiceRepository invoiceRepo = new InvoiceRepository();

        Client client = new Client("CLT-T04", "Cost Client", "cc@test.com", "pass", "+250788000007", "Muhanga");
        driverRepo.save(new Driver("DRV-T04", "Cost Driver", "cd@test.com", "pass", "RW-DL-666"));
        vehicleRepo.save(new Vehicle("VEH-T04", "RAW 666W", VehicleType.TRUCK, 5000));

        ShipmentService service = new ShipmentService(shipmentRepo, driverRepo, vehicleRepo, invoiceRepo);
        Shipment s = service.registerShipment("Kigali", "Muhanga", 200, 100, client);

        assert_(s.getCost() == 24000.0, "testCostCalculation: truck cost should be 24000 RWF, got " + s.getCost());
    }

    // ---- helpers ----
    static void assert_(boolean condition, String message) {
        if (condition) pass(message);
        else fail(message);
    }

    static void pass(String message) {
        System.out.println("  PASS: " + message);
        passed++;
    }

    static void fail(String message) {
        System.out.println("  FAIL: " + message);
        failed++;
    }
}
