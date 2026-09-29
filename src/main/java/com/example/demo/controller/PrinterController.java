package com.example.demo.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.PrinterRequestDTO;
import com.example.demo.dto.PrinterResponseDTO;
import com.example.demo.service.PrinterService;

@RestController
@RequestMapping("/api/impressoras")
public class PrinterController {

    private final PrinterService printerService;

    public PrinterController(PrinterService printerService) {
        this.printerService = printerService;
    }

    @GetMapping
    public ResponseEntity<List<PrinterResponseDTO>> listar() {
        return ResponseEntity.ok(printerService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrinterResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(printerService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PrinterResponseDTO> criar(@Valid @RequestBody PrinterRequestDTO dto) {
        PrinterResponseDTO response = printerService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrinterResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody PrinterRequestDTO dto) {
        return ResponseEntity.ok(printerService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        printerService.remover(id);
        return ResponseEntity.noContent().build();
    }
}