package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsDto;
import ru.practicum.StatsViewDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;
import ru.practicum.timeformatvalidation.TimeFormatValidator;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    private final TimeFormatValidator timeFormatValidator;


    @Transactional
    @Override
    public void postHit(StatsDto statsDto) {
        statsRepository.save(StatsMapper.toStats(statsDto));
    }

    @Override
    public List<StatsViewDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {

        LocalDateTime startTime = timeFormatValidator.parseToLocalDateTime(start);
        LocalDateTime endTime = timeFormatValidator.parseToLocalDateTime(end);
        List<StatsViewDto> dtos;

        if (uris != null) {
            if (unique) {
                dtos = statsRepository.findAllByTimeAndListOfUrisAndUniqueIp(startTime, endTime, uris);
            } else {
                dtos = statsRepository.findAllByTimeAndListOfUris(startTime, endTime, uris);
            }
        } else if (unique) {
            dtos = statsRepository.findAllByTimeAndUniqueIp(startTime, endTime);
        } else {
            dtos = statsRepository.findAllByTime(startTime, endTime);
        }

        return dtos;

    }

}