package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.models.Event;

import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.toEventShortDtos(compilation.getEvents()))
                .pinned(compilation.getIsPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation fromCompilationNewDto(CompilationNewDto dto, List<Event> events) {
        Compilation compilation = Compilation.builder()
                .events(events)
                .title(dto.getTitle())
                .build();
        if (dto.getPinned() != null) {
            compilation.setIsPinned(dto.getPinned());
        } else {
            compilation.setIsPinned(false);
        }
        return compilation;
    }

    public static List<CompilationDto> fromModelListToDtos(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

}