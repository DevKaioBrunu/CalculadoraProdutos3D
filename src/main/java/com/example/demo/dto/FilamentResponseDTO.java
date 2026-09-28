package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.entity.Filament;

public record FilamentResponseDTO(
        Long id,
        String name,
        String brand,
        String material,
        String color,
        BigDecimal pricePerKg,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static FilamentResponseDTO from(Filament filament) {
        return new FilamentResponseDTO(
                filament.getId(),
                filament.getName(),
                filament.getBrand(),
                filament.getMaterial(),
                filament.getColor(),
                filament.getPricePerKg(),
                filament.getCreatedAt(),
                filament.getUpdatedAt());
    }
}
