package ru.practicum.service;

import ru.practicum.StatsDto;
import ru.practicum.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void postHit(StatsDto statsDto);

    List<StatsViewDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}