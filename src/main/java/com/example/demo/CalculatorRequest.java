package com.example.demo;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CalculatorRequest(
        @NotNull @PositiveOrZero BigDecimal filamentWeightGrams,
        @NotNull @PositiveOrZero BigDecimal filamentPricePerKg,
        @NotNull @PositiveOrZero BigDecimal printTimeHours,
        @NotNull @PositiveOrZero BigDecimal printerPowerWatts,
        @NotNull @PositiveOrZero BigDecimal energyPricePerKwh,
        @NotNull @PositiveOrZero BigDecimal laborCost,
        @NotNull @PositiveOrZero BigDecimal otherCosts,
        @NotNull @PositiveOrZero BigDecimal profitMarginPercentage) {
}