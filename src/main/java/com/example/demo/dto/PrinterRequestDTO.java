package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PrinterRequestDTO(
        @NotBlank String name,
        @NotNull @Positive BigDecimal powerWatts,
        @Positive BigDecimal purchasePrice,
        @Positive BigDecimal estimatedLifespanHours,
        @Positive BigDecimal maintenanceCostPerHour) {
}