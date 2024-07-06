package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatsDto;
import ru.practicum.StatsViewDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@AllArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping(value = "/hit")
    public void postHit(@Valid @RequestBody StatsDto statsDto) {
        log.info("Start posting");
        statsService.postHit(statsDto);
    }

    @GetMapping("/stats")
    public List<StatsViewDto> getStatistics(@RequestParam String start,
                                            @RequestParam String end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(value = "unique", defaultValue = "false") String unique) {
        Boolean uniqueParam = Boolean.valueOf(unique);
        log.info("Start fetching statistics");
        List<StatsViewDto> fetchedStatistics = statsService.getStatistics(start, end, uris, uniqueParam);
        log.info("Finish fetching statistics");
        return fetchedStatistics;
    }

}