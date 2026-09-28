package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FilamentNotFoundException extends RuntimeException {

    public FilamentNotFoundException(Long id) {
        super("Filamento não encontrado: " + id);
    }
}
