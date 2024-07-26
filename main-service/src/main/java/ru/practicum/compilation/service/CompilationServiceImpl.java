package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.models.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;


    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException(CompilationDto.class, String.valueOf(compId),
                        "Подборка с id = " + compId + " не найдена"));

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto add(CompilationNewDto compilationNewDto) {
        List<Event> events = new ArrayList<>();

        if (compilationNewDto.getEvents() != null) {
            events = findEventsByIds(compilationNewDto.getEvents());
        }

        if (compilationNewDto.getPinned() == null) {
            compilationNewDto.setPinned(false);
        }

        Compilation compilation = CompilationMapper.fromCompilationNewDto(compilationNewDto, events);
        Compilation savedCompilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(savedCompilation);
    }

    @Transactional
    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException(CompilationDto.class, String.valueOf(compId),
                        "Подборка с id = " + compId + " не найдена"));

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setIsPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null && !compilation.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(findEventsByIds(updateCompilationRequest.getEvents()));
        }

        Compilation result = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(result);
    }

    @Transactional
    @Override
    public void deleteById(Long compId) {
        compilationRepository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException(CompilationDto.class, String.valueOf(compId),
                        "Подборка с id = " + compId + " не найдена"));

        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> search(Boolean pinned, Long from, Long size) {
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());

        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).getContent();
        } else if (pinned) {
            compilations = compilationRepository.findByIsPinnedTrue(pageable).getContent();
        } else {
            compilations = compilationRepository.findByIsPinnedFalse(pageable).getContent();
        }

        return CompilationMapper.fromModelListToDtos(compilations);
    }


    private List<Event> findEventsByIds(List<Long> ids) {
        return ids.stream()
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(Event.class, String.valueOf(id), "Событие с id = " + id + " не найдено")))
                .collect(Collectors.toList());
    }

}