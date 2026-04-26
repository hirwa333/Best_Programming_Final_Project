package com.impano.logistics.service;

import com.impano.logistics.factory.CostStrategyFactory;
import com.impano.logistics.factory.ShipmentFactory;
import com.impano.logistics.model.*;
import com.impano.logistics.repository.*;
import com.impano.logistics.strategy.CostCalculationStrategy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Shipment registerShipment(String origin, String destination,
                                     double weightKg, double distanceKm, Client client) {
        Shipment shipment = ShipmentFactory.createShipment(origin, destination, weightKg, distanceKm, client);

        Optional<Vehicle> vehicle = vehicleRepo.findAvailable();
        Optional<Driver> driver = driverRepo.findAvailable();

        if (vehicle.isPresent() && driver.isPresent()) {
            Vehicle v = vehicle.get();
            Driver d = driver.get();

            shipment.setVehicle(v);
            shipment.setDriver(d);
            shipment.setStatus(ShipmentStatus.ASSIGNED);

            v.setStatus(VehicleStatus.ON_TRIP);
            d.setAvailable(false);

            CostCalculationStrategy strategy = CostStrategyFactory.getStrategy(v.getType());
            double cost = strategy.calculate(shipment);
            shipment.setCost(cost);

            Invoice invoice = new Invoice(
                    "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    shipment, cost);
            invoiceRepo.save(invoice);
        }

        shipmentRepo.save(shipment);
        return shipment;
    }

    public boolean updateStatus(String shipmentId, ShipmentStatus newStatus) {
        Optional<Shipment> opt = shipmentRepo.findById(shipmentId);
        if (opt.isEmpty()) return false;

        Shipment shipment = opt.get();
        shipment.setStatus(newStatus);

        if (newStatus == ShipmentStatus.DELIVERED || newStatus == ShipmentStatus.CANCELLED) {
            if (shipment.getVehicle() != null) shipment.getVehicle().setStatus(VehicleStatus.AVAILABLE);
            if (shipment.getDriver() != null) shipment.getDriver().setAvailable(true);
        }
        return true;
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepo.findAll();
    }

    public Optional<Shipment> findById(String id) {
        return shipmentRepo.findById(id);
    }

    public List<Shipment> getShipmentsByClient(String clientId) {
        return shipmentRepo.findByClientId(clientId);
    }
}
