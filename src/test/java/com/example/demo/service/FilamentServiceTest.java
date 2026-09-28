package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.FilamentRequestDTO;
import com.example.demo.entity.Filament;
import com.example.demo.repository.FilamentRepository;

@ExtendWith(MockitoExtension.class)
class FilamentServiceTest {

    @Mock
    private FilamentRepository filamentRepository;

    @InjectMocks
    private FilamentService filamentService;

    @Test
    void createsFilamentAndMapsResponse() {
        Filament filament = new Filament("PLA Branco", "Marca", "PLA", "Branco", new BigDecimal("80.00"));
        when(filamentRepository.save(org.mockito.ArgumentMatchers.any(Filament.class))).thenReturn(filament);

        var response = filamentService.criar(request());

        assertThat(response.name()).isEqualTo("PLA Branco");
        assertThat(response.pricePerKg()).isEqualByComparingTo("80.00");
        verify(filamentRepository).save(org.mockito.ArgumentMatchers.any(Filament.class));
    }

    @Test
    void throwsWhenFilamentDoesNotExist() {
        when(filamentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filamentService.buscarPorId(10L))
                .isInstanceOf(FilamentNotFoundException.class);
    }

    private FilamentRequestDTO request() {
        return new FilamentRequestDTO("PLA Branco", "Marca", "PLA", "Branco", new BigDecimal("80.00"));
    }
}
