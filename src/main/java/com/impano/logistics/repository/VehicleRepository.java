package com.impano.logistics.repository;

import com.impano.logistics.model.Vehicle;
import com.impano.logistics.model.VehicleStatus;

import java.util.*;

public class VehicleRepository {
    private final Map<String, Vehicle> store = new HashMap<>();

    public void save(Vehicle vehicle) {
        store.put(vehicle.getVehicleId(), vehicle);
    }

    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Vehicle> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Vehicle> findAvailable() {
        return store.values().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .findFirst();
    }
}
