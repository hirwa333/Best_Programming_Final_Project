package com.impano.logistics.strategy;

import com.impano.logistics.model.Shipment;

/** Motorcycle pricing: base 1000 RWF + 50 RWF/km + 10 RWF/kg */
public class MotorcycleCostStrategy implements CostCalculationStrategy {
    @Override
    public double calculate(Shipment shipment) {
        return 1000 + (shipment.getDistanceKm() * 50) + (shipment.getWeightKg() * 10);
    }
}
