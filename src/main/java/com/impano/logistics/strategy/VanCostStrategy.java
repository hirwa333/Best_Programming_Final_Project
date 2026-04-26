package com.impano.logistics.strategy;

import com.impano.logistics.model.Shipment;

/** Van pricing: base 3000 RWF + 100 RWF/km + 15 RWF/kg */
public class VanCostStrategy implements CostCalculationStrategy {
    @Override
    public double calculate(Shipment shipment) {
        return 3000 + (shipment.getDistanceKm() * 100) + (shipment.getWeightKg() * 15);
    }
}
