package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Filament;

public interface FilamentRepository extends JpaRepository<Filament, Long> {
}
