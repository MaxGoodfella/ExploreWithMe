package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.enums.EventSort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
public class PublicEventController {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final EventService eventService;


    @GetMapping
    public List<EventShortDto> findEvents(@RequestParam(required = false) String text,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false) Boolean paid,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT)
                                              LocalDateTime rangeStart,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT)
                                              LocalDateTime rangeEnd,
                                          @RequestParam(required = false) Boolean onlyAvailable,
                                          @RequestParam(required = false) EventSort sort,
                                          @RequestParam(required = false, defaultValue = "0") Integer from,
                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                          HttpServletRequest request) {
        log.info("Start fetching events with many search parameters");
        List<EventShortDto> fetchedEvents = eventService.findEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
        log.info("Finish fetching events with many search parameters");
        return fetchedEvents;
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Start fetching event with id = {}", eventId);
        EventFullDto fetchedEvent = eventService.findEventById(eventId, request);
        log.info("Finish fetching event with id = {}", fetchedEvent.getId());
        return fetchedEvent;
    }

}