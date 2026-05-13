package com.impano.logistics.service;

import com.impano.logistics.factory.CostStrategyFactory;
import com.impano.logistics.factory.ShipmentFactory;
import com.impano.logistics.model.*;
import com.impano.logistics.repository.*;
import com.impano.logistics.strategy.CostCalculationStrategy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ShipmentService is the core business logic layer for shipment operations.
 *
 * It coordinates between repositories to:
 * - Create shipments using the Factory Pattern
 * - Auto-assign available drivers and vehicles
 * - Calculate shipping cost using the Strategy Pattern
 * - Auto-generate invoices upon shipment creation
 * - Update shipment status and release resources on delivery
 */
public class ShipmentService {

    private final ShipmentRepository shipmentRepo;
    private final DriverRepository driverRepo;
    private final VehicleRepository vehicleRepo;
    private final InvoiceRepository invoiceRepo;

    public ShipmentService(ShipmentRepository shipmentRepo, DriverRepository driverRepo,
                           VehicleRepository vehicleRepo, InvoiceRepository invoiceRepo) {
        this.shipmentRepo = shipmentRepo;
        this.driverRepo = driverRepo;
        this.vehicleRepo = vehicleRepo;
        this.invoiceRepo = invoiceRepo;
    }

    /**
     * Registers a new shipment and attempts to assign resources automatically.
     *
     * Steps:
     * 1. Use ShipmentFactory to create a shipment with a unique ID (Factory Pattern)
     * 2. Look for an available vehicle and driver
     * 3. If both found: assign them, calculate cost (Strategy Pattern), generate invoice
     * 4. If not found: save shipment as PENDING for later assignment
     *
     * @param origin      city where cargo is picked up
     * @param destination city where cargo is delivered
     * @param weightKg    weight of the cargo in kilograms
     * @param distanceKm  distance of the route in kilometers
     * @param client      the client who owns this shipment
     * @return the created Shipment object
     */
    public Shipment registerShipment(String origin, String destination,
                                     double weightKg, double distanceKm, Client client) {

        // Factory Pattern: centralized creation ensures unique ID and consistent state
        Shipment shipment = ShipmentFactory.createShipment(origin, destination, weightKg, distanceKm, client);

        // Check if resources are available for immediate assignment
        Optional<Vehicle> vehicle = vehicleRepo.findAvailable();
        Optional<Driver> driver = driverRepo.findAvailable();

        if (vehicle.isPresent() && driver.isPresent()) {
            Vehicle v = vehicle.get();
            Driver d = driver.get();

            // Assign driver and vehicle to this shipment
            shipment.setVehicle(v);
            shipment.setDriver(d);
            shipment.setStatus(ShipmentStatus.ASSIGNED);

            // Mark resources as busy so they are not assigned to another shipment
            v.setStatus(VehicleStatus.ON_TRIP);
            d.setAvailable(false);

            // Strategy Pattern: select pricing formula based on vehicle type
            // e.g. Truck uses TruckCostStrategy, Motorcycle uses MotorcycleCostStrategy
            CostCalculationStrategy strategy = CostStrategyFactory.getStrategy(v.getType());
            double cost = strategy.calculate(shipment);
            shipment.setCost(cost);

            // Auto-generate invoice immediately after cost is calculated
            Invoice invoice = new Invoice(
                    "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    shipment, cost);
            invoiceRepo.save(invoice);
        }
        // If no resources available, shipment stays PENDING — no invoice generated yet

        shipmentRepo.save(shipment);
        return shipment;
    }

    /**
     * Updates the status of an existing shipment.
     *
     * Important side effect: when status becomes DELIVERED or CANCELLED,
     * the assigned driver and vehicle are automatically freed for new shipments.
     *
     * @param shipmentId the ID of the shipment to update
     * @param newStatus  the new status to set
     * @return true if update was successful, false if shipment not found
     */
    public boolean updateStatus(String shipmentId, ShipmentStatus newStatus) {
        Optional<Shipment> opt = shipmentRepo.findById(shipmentId);
        if (opt.isEmpty()) return false;

        Shipment shipment = opt.get();
        shipment.setStatus(newStatus);

        // Free up driver and vehicle when shipment is completed or cancelled
        if (newStatus == ShipmentStatus.DELIVERED || newStatus == ShipmentStatus.CANCELLED) {
            if (shipment.getVehicle() != null) shipment.getVehicle().setStatus(VehicleStatus.AVAILABLE);
            if (shipment.getDriver() != null)  shipment.getDriver().setAvailable(true);
        }
        return true;
    }

    /** Returns all shipments in the system */
    public List<Shipment> getAllShipments() {
        return shipmentRepo.findAll();
    }

    /** Finds a shipment by its unique ID */
    public Optional<Shipment> findById(String id) {
        return shipmentRepo.findById(id);
    }

    /** Returns all shipments belonging to a specific client */
    public List<Shipment> getShipmentsByClient(String clientId) {
        return shipmentRepo.findByClientId(clientId);
    }
}
