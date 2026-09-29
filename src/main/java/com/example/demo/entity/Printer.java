package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "printers")
public class Printer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "power_watts", nullable = false, precision = 10, scale = 2)
    private BigDecimal powerWatts;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "estimated_lifespan_hours", precision = 12, scale = 2)
    private BigDecimal estimatedLifespanHours;

    @Column(name = "maintenance_cost_per_hour", precision = 10, scale = 2)
    private BigDecimal maintenanceCostPerHour;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Printer() {
    }

    public Printer(String name, BigDecimal powerWatts, BigDecimal purchasePrice,
            BigDecimal estimatedLifespanHours, BigDecimal maintenanceCostPerHour) {
        this.name = name;
        this.powerWatts = powerWatts;
        this.purchasePrice = purchasePrice;
        this.estimatedLifespanHours = estimatedLifespanHours;
        this.maintenanceCostPerHour = maintenanceCostPerHour;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPowerWatts() {
        return powerWatts;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public BigDecimal getEstimatedLifespanHours() {
        return estimatedLifespanHours;
    }

    public BigDecimal getMaintenanceCostPerHour() {
        return maintenanceCostPerHour;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(String name, BigDecimal powerWatts, BigDecimal purchasePrice,
            BigDecimal estimatedLifespanHours, BigDecimal maintenanceCostPerHour) {
        this.name = name;
        this.powerWatts = powerWatts;
        this.purchasePrice = purchasePrice;
        this.estimatedLifespanHours = estimatedLifespanHours;
        this.maintenanceCostPerHour = maintenanceCostPerHour;
    }
}