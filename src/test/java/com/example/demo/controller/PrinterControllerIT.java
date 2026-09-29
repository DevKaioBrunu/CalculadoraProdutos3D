package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.demo.PostgresIntegrationTestSupport;
import com.example.demo.dto.PrinterRequestDTO;
import com.example.demo.dto.PrinterResponseDTO;
import com.example.demo.repository.PrinterRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class PrinterControllerIT extends PostgresIntegrationTestSupport {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private PrinterRepository printerRepository;

    @BeforeEach
    void cleanDatabase() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
        printerRepository.deleteAll();
    }

    @Test
    void createsValidPrinter() {
        ResponseEntity<PrinterResponseDTO> response = restClient.post()
                .uri("/api/impressoras")
                .body(request("Bambu Lab A1", "350.00"))
                .retrieve()
                .toEntity(PrinterResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Bambu Lab A1");
        assertThat(response.getBody().powerWatts()).isEqualByComparingTo("350.00");
        assertThat(response.getBody().createdAt()).isNotNull();
        assertThat(response.getBody().updatedAt()).isNotNull();
    }

    @Test
    void rejectsInvalidPrinter() {
        PrinterRequestDTO invalidRequest = new PrinterRequestDTO(
                "", new BigDecimal("-1.00"), null, null, null);

        ResponseEntity<String> response = restClient.post()
                .uri("/api/impressoras")
                .body(invalidRequest)
                .retrieve()
                .onStatus(status -> status.isError(), (request, responseError) -> {
                })
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findsExistingPrinter() {
        PrinterResponseDTO created = createPrinter();

        ResponseEntity<PrinterResponseDTO> response = restClient.get()
                .uri("/api/impressoras/{id}", created.id())
                .retrieve()
                .toEntity(PrinterResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
    }

    @Test
    void returnsNotFoundForMissingPrinter() {
        ResponseEntity<String> response = restClient.get()
                .uri("/api/impressoras/{id}", 999999L)
                .retrieve()
                .onStatus(status -> status.isError(), (request, responseError) -> {
                })
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updatesExistingPrinter() {
        PrinterResponseDTO created = createPrinter();
        PrinterRequestDTO update = request("Bambu Lab P1S", "500.00");

        ResponseEntity<PrinterResponseDTO> response = restClient.put()
                .uri("/api/impressoras/{id}", created.id())
                .body(update)
                .retrieve()
                .toEntity(PrinterResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Bambu Lab P1S");
        assertThat(response.getBody().powerWatts()).isEqualByComparingTo("500.00");
    }

    @Test
    void deletesExistingPrinter() {
        PrinterResponseDTO created = createPrinter();

        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/api/impressoras/{id}", created.id())
                .retrieve()
                .toBodilessEntity();
        ResponseEntity<String> getResponse = restClient.get()
                .uri("/api/impressoras/{id}", created.id())
                .retrieve()
                .onStatus(status -> status.isError(), (request, responseError) -> {
                })
                .toEntity(String.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private PrinterResponseDTO createPrinter() {
        ResponseEntity<PrinterResponseDTO> response = restClient.post()
                .uri("/api/impressoras")
                .body(request("Bambu Lab A1", "350.00"))
                .retrieve()
                .toEntity(PrinterResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private PrinterRequestDTO request(String name, String powerWatts) {
        return new PrinterRequestDTO(
                name,
                new BigDecimal(powerWatts),
                new BigDecimal("2500.00"),
                new BigDecimal("10000.00"),
                new BigDecimal("0.50"));
    }
}