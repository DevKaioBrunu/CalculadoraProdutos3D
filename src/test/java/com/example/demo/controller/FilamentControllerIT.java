package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.PostgresIntegrationTestSupport;
import com.example.demo.dto.FilamentRequestDTO;
import com.example.demo.dto.FilamentResponseDTO;
import com.example.demo.repository.FilamentRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class FilamentControllerIT extends PostgresIntegrationTestSupport {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private FilamentRepository filamentRepository;

    @BeforeEach
    void cleanDatabase() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .build();
        filamentRepository.deleteAll();
    }

    @Test
    void createsValidFilament() {
        ResponseEntity<FilamentResponseDTO> response = restClient.post()
            .uri("/api/filamentos")
            .body(request("PLA Branco", "80.00"))
            .retrieve()
            .toEntity(FilamentResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("PLA Branco");
        assertThat(response.getBody().pricePerKg()).isEqualByComparingTo("80.00");
        assertThat(response.getBody().createdAt()).isNotNull();
        assertThat(response.getBody().updatedAt()).isNotNull();
    }

    @Test
    void rejectsInvalidFilament() {
        FilamentRequestDTO invalidRequest = new FilamentRequestDTO(
                "", "", "", null, new BigDecimal("-1.00"));

        ResponseEntity<String> response = restClient.post()
            .uri("/api/filamentos")
            .body(invalidRequest)
            .retrieve()
            .onStatus(status -> status.isError(), (request, responseError) -> {
            })
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findsExistingFilament() {
        FilamentResponseDTO created = createFilament();

        ResponseEntity<FilamentResponseDTO> response = restClient.get()
            .uri("/api/filamentos/{id}", created.id())
            .retrieve()
            .toEntity(FilamentResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(created.id());
    }

    @Test
    void returnsNotFoundForMissingFilament() {
        ResponseEntity<String> response = restClient.get()
            .uri("/api/filamentos/{id}", 999999L)
            .retrieve()
            .onStatus(status -> status.isError(), (request, responseError) -> {
            })
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updatesExistingFilament() {
        FilamentResponseDTO created = createFilament();
        FilamentRequestDTO update = request("PETG Preto", "95.50");

        ResponseEntity<FilamentResponseDTO> response = restClient.put()
            .uri("/api/filamentos/{id}", created.id())
            .body(update)
            .retrieve()
            .toEntity(FilamentResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("PETG Preto");
        assertThat(response.getBody().pricePerKg()).isEqualByComparingTo("95.50");
    }

    @Test
    void deletesExistingFilament() {
        FilamentResponseDTO created = createFilament();

        ResponseEntity<Void> deleteResponse = restClient.delete()
            .uri("/api/filamentos/{id}", created.id())
            .retrieve()
            .toBodilessEntity();
        ResponseEntity<String> getResponse = restClient.get()
            .uri("/api/filamentos/{id}", created.id())
            .retrieve()
            .onStatus(status -> status.isError(), (request, responseError) -> {
            })
            .toEntity(String.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private FilamentResponseDTO createFilament() {
        ResponseEntity<FilamentResponseDTO> response = restClient.post()
            .uri("/api/filamentos")
            .body(request("PLA Branco", "80.00"))
            .retrieve()
            .toEntity(FilamentResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private FilamentRequestDTO request(String name, String pricePerKg) {
        return new FilamentRequestDTO(
                name, "Marca Teste", "PLA", "Branco", new BigDecimal(pricePerKg));
    }
}