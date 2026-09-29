package com.example.demo;

import java.math.BigDecimal;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Request for price calculation. When an ID is provided, its registered value
 * takes precedence over the corresponding manual value.
 */
public record CalculatorRequest(
        @NotNull @PositiveOrZero BigDecimal filamentWeightGrams,
                @PositiveOrZero BigDecimal filamentPricePerKg,
        @NotNull @PositiveOrZero BigDecimal printTimeHours,
                @PositiveOrZero BigDecimal printerPowerWatts,
        @NotNull @PositiveOrZero BigDecimal energyPricePerKwh,
        @NotNull @PositiveOrZero BigDecimal laborCost,
        @NotNull @PositiveOrZero BigDecimal otherCosts,
                @NotNull @PositiveOrZero BigDecimal profitMarginPercentage,
                Long filamentId,
                Long printerId) {

        public CalculatorRequest(
                        BigDecimal filamentWeightGrams,
                        BigDecimal filamentPricePerKg,
                        BigDecimal printTimeHours,
                        BigDecimal printerPowerWatts,
                        BigDecimal energyPricePerKwh,
                        BigDecimal laborCost,
                        BigDecimal otherCosts,
                        BigDecimal profitMarginPercentage) {
                this(filamentWeightGrams, filamentPricePerKg, printTimeHours, printerPowerWatts,
                                energyPricePerKwh, laborCost, otherCosts, profitMarginPercentage, null, null);
        }

        @AssertTrue(message = "filamentPricePerKg or filamentId must be provided")
        public boolean hasFilamentSource() {
                return filamentPricePerKg != null || filamentId != null;
        }

        @AssertTrue(message = "printerPowerWatts or printerId must be provided")
        public boolean hasPrinterSource() {
                return printerPowerWatts != null || printerId != null;
        }
}