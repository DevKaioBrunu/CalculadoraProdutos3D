package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.demo.entity.Filament;
import com.example.demo.entity.Printer;
import com.example.demo.repository.FilamentRepository;
import com.example.demo.repository.PrinterRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CalculatorHybridControllerIT extends PostgresIntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Autowired
    private FilamentRepository filamentRepository;

    @Autowired
    private PrinterRepository printerRepository;

    private RestClient restClient;

    @BeforeEach
    void cleanDatabase() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
        filamentRepository.deleteAll();
        printerRepository.deleteAll();
    }

    @Test
    void calculatesWithFilamentIdAndManualPrinterPower() {
        Filament filament = filamentRepository.save(
                new Filament("PLA", "Teste", "PLA", "Branco", new BigDecimal("80.00")));

        CalculatorRequest request = request(
                null, new BigDecimal("200.00"), filament.getId(), null);

        CalculatorResponse response = calculate(request, HttpStatus.OK).getBody();

        assertThat(response).isNotNull();
        assertThat(response.filamentCost()).isEqualByComparingTo("40.00");
        assertThat(response.energyCost()).isEqualByComparingTo("2.00");
    }

    @Test
    void calculatesWithPrinterIdAndManualFilamentPrice() {
        Printer printer = printerRepository.save(
                new Printer("Teste", new BigDecimal("200.00"), null, null, null));

        CalculatorRequest request = request(
                new BigDecimal("80.00"), null, null, printer.getId());

        CalculatorResponse response = calculate(request, HttpStatus.OK).getBody();

        assertThat(response).isNotNull();
        assertThat(response.filamentCost()).isEqualByComparingTo("40.00");
        assertThat(response.energyCost()).isEqualByComparingTo("2.00");
    }

    @Test
    void calculatesWithBothCatalogIds() {
        Filament filament = filamentRepository.save(
                new Filament("PLA", "Teste", "PLA", "Branco", new BigDecimal("80.00")));
        Printer printer = printerRepository.save(
                new Printer("Teste", new BigDecimal("200.00"), null, null, null));

        CalculatorRequest request = request(
                new BigDecimal("999.00"), new BigDecimal("999.00"), filament.getId(), printer.getId());

        CalculatorResponse response = calculate(request, HttpStatus.OK).getBody();

        assertThat(response).isNotNull();
        assertThat(response.filamentCost()).isEqualByComparingTo("40.00");
        assertThat(response.energyCost()).isEqualByComparingTo("2.00");
        assertThat(response.totalCost()).isEqualByComparingTo("57.00");
        assertThat(response.salePrice()).isEqualByComparingTo("85.50");
    }

    @Test
    void returnsNotFoundForMissingFilamentId() {
        CalculatorRequest request = request(
                new BigDecimal("80.00"), new BigDecimal("200.00"), 999999L, null);

        ResponseEntity<String> response = restClient.post()
                .uri("/api/calculadora/precificar")
                .body(request)
                .retrieve()
                .onStatus(status -> status.isError(), (requestMessage, responseMessage) -> {
                })
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<CalculatorResponse> calculate(
            CalculatorRequest request, HttpStatus expectedStatus) {
        ResponseEntity<CalculatorResponse> response = restClient.post()
                .uri("/api/calculadora/precificar")
                .body(request)
                .retrieve()
                .toEntity(CalculatorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        return response;
    }

    private CalculatorRequest request(
            BigDecimal filamentPricePerKg, BigDecimal printerPowerWatts,
            Long filamentId, Long printerId) {
        return new CalculatorRequest(
                new BigDecimal("500"),
                filamentPricePerKg,
                new BigDecimal("10"),
                printerPowerWatts,
                new BigDecimal("1"),
                new BigDecimal("10"),
                new BigDecimal("5"),
                new BigDecimal("50"),
                filamentId,
                printerId);
    }
}