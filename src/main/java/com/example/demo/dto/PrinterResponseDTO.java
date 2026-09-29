package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.entity.Printer;

public record PrinterResponseDTO(
        Long id,
        String name,
        BigDecimal powerWatts,
        BigDecimal purchasePrice,
        BigDecimal estimatedLifespanHours,
        BigDecimal maintenanceCostPerHour,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static PrinterResponseDTO from(Printer printer) {
        return new PrinterResponseDTO(
                printer.getId(),
                printer.getName(),
                printer.getPowerWatts(),
                printer.getPurchasePrice(),
                printer.getEstimatedLifespanHours(),
                printer.getMaintenanceCostPerHour(),
                printer.getCreatedAt(),
                printer.getUpdatedAt());
    }
}