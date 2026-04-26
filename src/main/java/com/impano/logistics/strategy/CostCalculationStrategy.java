package com.impano.logistics.strategy;

import com.impano.logistics.model.Shipment;

/**
 * Strategy Pattern: defines a family of cost calculation algorithms.
 * Each vehicle type uses a different pricing strategy.
 */
public interface CostCalculationStrategy {
    double calculate(Shipment shipment);
}
