package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FilamentRequestDTO(
        @NotBlank String name,
        @NotBlank String brand,
        @NotBlank String material,
        String color,
        @NotNull @Positive BigDecimal pricePerKg) {
}
