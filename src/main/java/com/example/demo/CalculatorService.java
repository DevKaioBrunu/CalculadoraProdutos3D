package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Filament;
import com.example.demo.entity.Printer;
import com.example.demo.repository.FilamentRepository;
import com.example.demo.repository.PrinterRepository;
import com.example.demo.service.FilamentNotFoundException;
import com.example.demo.service.PrinterNotFoundException;

@Service
public class CalculatorService {

    private static final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

        private final FilamentRepository filamentRepository;
        private final PrinterRepository printerRepository;

        public CalculatorService() {
                this.filamentRepository = null;
                this.printerRepository = null;
        }

        @Autowired
        public CalculatorService(FilamentRepository filamentRepository, PrinterRepository printerRepository) {
                this.filamentRepository = filamentRepository;
                this.printerRepository = printerRepository;
        }

    public CalculatorResponse calculate(CalculatorRequest request) {
                BigDecimal filamentPricePerKg = request.filamentPricePerKg();
                if (request.filamentId() != null) {
                        ensureRepositoriesAvailable();
                        Filament filament = filamentRepository.findById(request.filamentId())
                                        .orElseThrow(() -> new FilamentNotFoundException(request.filamentId()));
                        filamentPricePerKg = filament.getPricePerKg();
                }

                BigDecimal printerPowerWatts = request.printerPowerWatts();
                if (request.printerId() != null) {
                        ensureRepositoriesAvailable();
                        Printer printer = printerRepository.findById(request.printerId())
                                        .orElseThrow(() -> new PrinterNotFoundException(request.printerId()));
                        printerPowerWatts = printer.getPowerWatts();
                }

                return calculateWithValues(request, filamentPricePerKg, printerPowerWatts);
        }

        private CalculatorResponse calculateWithValues(
                        CalculatorRequest request, BigDecimal filamentPricePerKg, BigDecimal printerPowerWatts) {
        BigDecimal filamentCost = request.filamentWeightGrams()
                .divide(ONE_THOUSAND, 8, RoundingMode.HALF_UP)
                                .multiply(filamentPricePerKg);
                BigDecimal energyCost = printerPowerWatts
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

        private void ensureRepositoriesAvailable() {
                if (filamentRepository == null || printerRepository == null) {
                        throw new IllegalStateException("Repositories are required when using catalog IDs");
                }
        }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}