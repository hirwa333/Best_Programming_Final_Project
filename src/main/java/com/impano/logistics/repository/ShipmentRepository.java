package com.impano.logistics.repository;

import com.impano.logistics.model.Shipment;
import com.impano.logistics.model.ShipmentStatus;

import java.util.*;
import java.util.stream.Collectors;

public class ShipmentRepository {
    private final Map<String, Shipment> store = new HashMap<>();

    public void save(Shipment shipment) {
        store.put(shipment.getShipmentId(), shipment);
    }

    public Optional<Shipment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Shipment> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Shipment> findByStatus(ShipmentStatus status) {
        return store.values().stream()
                .filter(s -> s.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Shipment> findByClientId(String clientId) {
        return store.values().stream()
                .filter(s -> s.getClient().getUserId().equals(clientId))
                .collect(Collectors.toList());
    }
}
