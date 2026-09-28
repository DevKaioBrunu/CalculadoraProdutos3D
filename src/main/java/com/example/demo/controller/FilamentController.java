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

import com.example.demo.dto.FilamentRequestDTO;
import com.example.demo.dto.FilamentResponseDTO;
import com.example.demo.service.FilamentService;

@RestController
@RequestMapping("/api/filamentos")
public class FilamentController {

    private final FilamentService filamentService;

    public FilamentController(FilamentService filamentService) {
        this.filamentService = filamentService;
    }

    @GetMapping
    public ResponseEntity<List<FilamentResponseDTO>> listar() {
        return ResponseEntity.ok(filamentService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilamentResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(filamentService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FilamentResponseDTO> criar(@Valid @RequestBody FilamentRequestDTO dto) {
        FilamentResponseDTO response = filamentService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilamentResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody FilamentRequestDTO dto) {
        return ResponseEntity.ok(filamentService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        filamentService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
