package com.impano.logistics.model;

public class Vehicle {
    private String vehicleId;
    private String plateNumber;
    private VehicleType type;
    private double capacityKg;
    private VehicleStatus status;

    public Vehicle(String vehicleId, String plateNumber, VehicleType type, double capacityKg) {
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.type = type;
        this.capacityKg = capacityKg;
        this.status = VehicleStatus.AVAILABLE;
    }

    public String getVehicleId() { return vehicleId; }
    public String getPlateNumber() { return plateNumber; }
    public VehicleType getType() { return type; }
    public double getCapacityKg() { return capacityKg; }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Vehicle{id=%s, plate=%s, type=%s, capacity=%.1fkg, status=%s}",
                vehicleId, plateNumber, type, capacityKg, status);
    }
}
