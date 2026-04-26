package com.impano.logistics.factory;

import com.impano.logistics.model.VehicleType;
import com.impano.logistics.strategy.*;

/**
 * Factory Pattern: returns the correct CostCalculationStrategy based on vehicle type.
 */
public class CostStrategyFactory {

    private CostStrategyFactory() {}

    public static CostCalculationStrategy getStrategy(VehicleType type) {
        switch (type) {
            case TRUCK:
            case PICKUP:
                return new TruckCostStrategy();
            case VAN:
                return new VanCostStrategy();
            case MOTORCYCLE:
                return new MotorcycleCostStrategy();
            default:
                return new VanCostStrategy();
        }
    }
}
