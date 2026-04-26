package com.impano.logistics.repository;

import com.impano.logistics.model.Driver;

import java.util.*;

public class DriverRepository {
    private final Map<String, Driver> store = new HashMap<>();

    public void save(Driver driver) {
        store.put(driver.getUserId(), driver);
    }

    public Optional<Driver> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Driver> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Driver> findAvailable() {
        return store.values().stream().filter(Driver::isAvailable).findFirst();
    }
}
