package com.impano.logistics.service;

import com.impano.logistics.model.Vehicle;
import com.impano.logistics.model.VehicleType;
import com.impano.logistics.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VehicleService {
    private final VehicleRepository vehicleRepo;

    public VehicleService(VehicleRepository vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    public Vehicle addVehicle(String plateNumber, VehicleType type, double capacityKg) {
        String id = "VEH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Vehicle vehicle = new Vehicle(id, plateNumber, type, capacityKg);
        vehicleRepo.save(vehicle);
        return vehicle;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepo.findAll();
    }

    public Optional<Vehicle> findById(String id) {
        return vehicleRepo.findById(id);
    }
}
