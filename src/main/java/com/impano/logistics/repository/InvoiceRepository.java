package com.impano.logistics.repository;

import com.impano.logistics.model.Invoice;

import java.util.*;

public class InvoiceRepository {
    private final Map<String, Invoice> store = new HashMap<>();

    public void save(Invoice invoice) {
        store.put(invoice.getInvoiceId(), invoice);
    }

    public Optional<Invoice> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Invoice> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Invoice> findByShipmentId(String shipmentId) {
        return store.values().stream()
                .filter(i -> i.getShipment().getShipmentId().equals(shipmentId))
                .findFirst();
    }
}
