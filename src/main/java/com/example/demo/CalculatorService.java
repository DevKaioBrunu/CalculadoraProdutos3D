package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    private static final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public CalculatorResponse calculate(CalculatorRequest request) {
        BigDecimal filamentCost = request.filamentWeightGrams()
                .divide(ONE_THOUSAND, 8, RoundingMode.HALF_UP)
                .multiply(request.filamentPricePerKg());
        BigDecimal energyCost = request.printerPowerWatts()
                .divide(ONE_THOUSAND, 8, RoundingMode.HALF_UP)
                .multiply(request.printTimeHours())
                .multiply(request.energyPricePerKwh());
        BigDecimal totalCost = filamentCost
                .add(energyCost)
                .add(request.laborCost())
                .add(request.otherCosts());
        BigDecimal salePrice = totalCost.multiply(
                BigDecimal.ONE.add(request.profitMarginPercentage()
                        .divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP)));

        return new CalculatorResponse(
                money(filamentCost),
                money(energyCost),
                money(totalCost),
                money(salePrice));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}