package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatsDto;
import ru.practicum.StatsViewDto;
import ru.practicum.exceptions.IllegalArgumentException;
import ru.practicum.exceptions.MissingServletParameterException;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class StatsController {

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final StatsService statsService;


    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid StatsDto statsDto) {
        log.info("Start posting");
        statsService.postHit(statsDto);
    }

    @GetMapping("/stats")
    public List<StatsViewDto> getStatistics(@RequestParam(required = false) String start,
                                            @RequestParam(required = false) String end,
                                            @RequestParam(required = false, defaultValue = "") List<String> uris,
                                            @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {

        log.info("Start fetching statistics");

        if (start == null) {
            throw new MissingServletParameterException("start");
        }
        if (end == null) {
            throw new MissingServletParameterException("end");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        try {
            startDateTime = LocalDateTime.parse(start, formatter);
            endDateTime = LocalDateTime.parse(end, formatter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("date", "Некорректный формат даты. Используйте: 'yyyy-MM-dd HH:mm:ss'.");
        }

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("start", "Время начала не может быть позже времени конца.");
        }

        if (uris.isEmpty() || (uris.size() == 1 && uris.get(0).isEmpty())) {
            uris = new ArrayList<>();
        }

        List<StatsViewDto> fetchedStatistics = statsService.getStatistics(startDateTime, endDateTime, uris, unique);

        log.info("Finish fetching statistics");

        return fetchedStatistics;

    }

}