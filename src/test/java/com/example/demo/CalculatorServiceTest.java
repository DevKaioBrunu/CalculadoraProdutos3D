package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();

    @Test
    void calculatesCostsAndSalePrice() {
        CalculatorRequest request = new CalculatorRequest(
                new BigDecimal("500"),
                new BigDecimal("80"),
                new BigDecimal("10"),
                new BigDecimal("200"),
                new BigDecimal("1"),
                new BigDecimal("10"),
                new BigDecimal("5"),
                new BigDecimal("50"));

        CalculatorResponse response = calculatorService.calculate(request);

        assertThat(response.filamentCost()).isEqualByComparingTo("40.00");
        assertThat(response.energyCost()).isEqualByComparingTo("2.00");
        assertThat(response.totalCost()).isEqualByComparingTo("57.00");
        assertThat(response.salePrice()).isEqualByComparingTo("85.50");
    }
}