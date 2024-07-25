package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsDto;
import ru.practicum.StatsViewDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;


    @Transactional
    @Override
    public void postHit(StatsDto statsDto) {
        statsRepository.save(StatsMapper.toStats(statsDto));
    }

    @Override
    public List<StatsViewDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

            if (uris.isEmpty()) {
                if (unique) {
                    return statsRepository.findStatsUniqueIp(start, end);
                } else {
                    return statsRepository.findStats(start, end);
                }
            } else {
                if (unique) {
                    return statsRepository.findStatsByUriUniqueIp(uris, start, end);
                } else {
                    return statsRepository.findStatsByUri(uris, start, end);
                }
            }

    }

}