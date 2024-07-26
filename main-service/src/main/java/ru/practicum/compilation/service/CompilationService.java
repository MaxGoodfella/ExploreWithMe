package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;


public interface CompilationService {

    CompilationDto findById(Long compId);

    List<CompilationDto> search(Boolean pinned, Long from, Long size);

    CompilationDto add(CompilationNewDto compilationDto);

    void deleteById(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest update);

}