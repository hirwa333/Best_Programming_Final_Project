package com.impano.logistics.model;

import java.time.LocalDateTime;

public class Shipment {
    private String shipmentId;
    private String origin;
    private String destination;
    private double weightKg;
    private double distanceKm;
    private ShipmentStatus status;
    private Client client;
    private Driver driver;
    private Vehicle vehicle;
    private double cost;
    private LocalDateTime createdAt;

    public Shipment(String shipmentId, String origin, String destination,
                    double weightKg, double distanceKm, Client client) {
        this.shipmentId = shipmentId;
        this.origin = origin;
        this.destination = destination;
        this.weightKg = weightKg;
        this.distanceKm = distanceKm;
        this.client = client;
        this.status = ShipmentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getShipmentId() { return shipmentId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public double getWeightKg() { return weightKg; }
    public double getDistanceKm() { return distanceKm; }
    public ShipmentStatus getStatus() { return status; }
    public Client getClient() { return client; }
    public Driver getDriver() { return driver; }
    public Vehicle getVehicle() { return vehicle; }
    public double getCost() { return cost; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(ShipmentStatus status) { this.status = status; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public void setCost(double cost) { this.cost = cost; }

    @Override
    public String toString() {
        return String.format(
            "Shipment{id=%s, from=%s, to=%s, weight=%.1fkg, dist=%.1fkm, status=%s, cost=%.2f RWF, client=%s}",
            shipmentId, origin, destination, weightKg, distanceKm, status, cost, client.getName());
    }
}
