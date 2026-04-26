package com.impano.logistics.factory;

import com.impano.logistics.model.Client;
import com.impano.logistics.model.Shipment;

import java.util.UUID;

/**
 * Factory Pattern: centralizes Shipment object creation.
 * Ensures every shipment gets a unique ID and consistent initialization.
 */
public class ShipmentFactory {

    private ShipmentFactory() {}

    public static Shipment createShipment(String origin, String destination,
                                          double weightKg, double distanceKm, Client client) {
        String id = "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Shipment(id, origin, destination, weightKg, distanceKm, client);
    }
}
