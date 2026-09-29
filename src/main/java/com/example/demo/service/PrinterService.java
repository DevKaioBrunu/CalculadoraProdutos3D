package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PrinterRequestDTO;
import com.example.demo.dto.PrinterResponseDTO;
import com.example.demo.entity.Printer;
import com.example.demo.repository.PrinterRepository;

@Service
public class PrinterService {

    private final PrinterRepository printerRepository;

    public PrinterService(PrinterRepository printerRepository) {
        this.printerRepository = printerRepository;
    }

    @Transactional(readOnly = true)
    public List<PrinterResponseDTO> listar() {
        return printerRepository.findAll().stream()
                .map(PrinterResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrinterResponseDTO buscarPorId(Long id) {
        return PrinterResponseDTO.from(findById(id));
    }

    @Transactional
    public PrinterResponseDTO criar(PrinterRequestDTO dto) {
        Printer printer = new Printer(
                dto.name(), dto.powerWatts(), dto.purchasePrice(),
                dto.estimatedLifespanHours(), dto.maintenanceCostPerHour());
        return PrinterResponseDTO.from(printerRepository.save(printer));
    }

    @Transactional
    public PrinterResponseDTO atualizar(Long id, PrinterRequestDTO dto) {
        Printer printer = findById(id);
        printer.update(dto.name(), dto.powerWatts(), dto.purchasePrice(),
                dto.estimatedLifespanHours(), dto.maintenanceCostPerHour());
        return PrinterResponseDTO.from(printerRepository.save(printer));
    }

    @Transactional
    public void remover(Long id) {
        Printer printer = findById(id);
        printerRepository.delete(printer);
    }

    private Printer findById(Long id) {
        return printerRepository.findById(id)
                .orElseThrow(() -> new PrinterNotFoundException(id));
    }
}