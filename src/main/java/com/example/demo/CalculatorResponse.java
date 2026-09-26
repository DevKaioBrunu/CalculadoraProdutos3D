package com.example.demo;

import java.math.BigDecimal;

public record CalculatorResponse(
        BigDecimal filamentCost,
        BigDecimal energyCost,
        BigDecimal totalCost,
        BigDecimal salePrice) {
}