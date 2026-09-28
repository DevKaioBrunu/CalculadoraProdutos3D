package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.FilamentRequestDTO;
import com.example.demo.dto.FilamentResponseDTO;
import com.example.demo.entity.Filament;
import com.example.demo.repository.FilamentRepository;

@Service
public class FilamentService {

    private final FilamentRepository filamentRepository;

    public FilamentService(FilamentRepository filamentRepository) {
        this.filamentRepository = filamentRepository;
    }

    @Transactional(readOnly = true)
    public List<FilamentResponseDTO> listar() {
        return filamentRepository.findAll().stream()
                .map(FilamentResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FilamentResponseDTO buscarPorId(Long id) {
        return FilamentResponseDTO.from(findById(id));
    }

    @Transactional
    public FilamentResponseDTO criar(FilamentRequestDTO dto) {
        Filament filament = new Filament(
                dto.name(), dto.brand(), dto.material(), dto.color(), dto.pricePerKg());
        return FilamentResponseDTO.from(filamentRepository.save(filament));
    }

    @Transactional
    public FilamentResponseDTO atualizar(Long id, FilamentRequestDTO dto) {
        Filament filament = findById(id);
        filament.update(dto.name(), dto.brand(), dto.material(), dto.color(), dto.pricePerKg());
        return FilamentResponseDTO.from(filamentRepository.save(filament));
    }

    @Transactional
    public void remover(Long id) {
        Filament filament = findById(id);
        filamentRepository.delete(filament);
    }

    private Filament findById(Long id) {
        return filamentRepository.findById(id)
                .orElseThrow(() -> new FilamentNotFoundException(id));
    }
}
