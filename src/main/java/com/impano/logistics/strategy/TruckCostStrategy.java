package com.impano.logistics.strategy;

import com.impano.logistics.model.Shipment;

/** Truck pricing: base 5000 RWF + 150 RWF/km + 20 RWF/kg */
public class TruckCostStrategy implements CostCalculationStrategy {
    @Override
    public double calculate(Shipment shipment) {
        return 5000 + (shipment.getDistanceKm() * 150) + (shipment.getWeightKg() * 20);
    }
}
