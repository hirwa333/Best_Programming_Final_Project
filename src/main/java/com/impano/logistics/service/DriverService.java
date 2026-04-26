package com.impano.logistics.service;

import com.impano.logistics.model.Driver;
import com.impano.logistics.repository.DriverRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DriverService {
    private final DriverRepository driverRepo;

    public DriverService(DriverRepository driverRepo) {
        this.driverRepo = driverRepo;
    }

    public Driver registerDriver(String name, String email, String password, String licenseNumber) {
        String id = "DRV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Driver driver = new Driver(id, name, email, password, licenseNumber);
        driverRepo.save(driver);
        return driver;
    }

    public List<Driver> getAllDrivers() {
        return driverRepo.findAll();
    }

    public Optional<Driver> findById(String id) {
        return driverRepo.findById(id);
    }
}
