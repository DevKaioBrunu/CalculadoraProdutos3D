package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Printer;

public interface PrinterRepository extends JpaRepository<Printer, Long> {
}