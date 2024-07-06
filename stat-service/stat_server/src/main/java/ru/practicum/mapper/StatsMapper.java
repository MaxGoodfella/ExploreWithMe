package ru.practicum.mapper;

import ru.practicum.StatsDto;
import ru.practicum.model.Stats;


public class StatsMapper {

    public static Stats toStats(StatsDto statsDto) {
        return Stats.builder()
                .app(statsDto.getApp())
                .uri(statsDto.getUri())
                .ip(statsDto.getIp())
                .timestamp(statsDto.getTimestamp())
                .build();
    }

}