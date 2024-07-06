package ru.practicum.service;

import ru.practicum.StatsDto;
import ru.practicum.StatsViewDto;

import java.util.List;

public interface StatsService {

    void postHit(StatsDto inDto);

    List<StatsViewDto> getStatistics(String start, String end, List<String> uris, Boolean unique);

}