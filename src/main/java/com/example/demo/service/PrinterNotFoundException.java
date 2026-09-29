package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PrinterNotFoundException extends RuntimeException {

    public PrinterNotFoundException(Long id) {
        super("Impressora não encontrada: " + id);
    }
}