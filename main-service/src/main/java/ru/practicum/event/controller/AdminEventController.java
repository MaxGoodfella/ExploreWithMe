package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventAdmin;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.enums.EventStatus;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;


@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final EventService eventService;


    @GetMapping
    public List<EventFullDto> findListByAdmin(@RequestParam(required = false) List<Long> users,
                                              @RequestParam(required = false) List<EventStatus> states,
                                              @RequestParam(required = false) List<Long> categories,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT)
                                                  LocalDateTime rangeStart,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT)
                                                  LocalDateTime rangeEnd,
                                              @RequestParam(required = false, defaultValue = "0") @PositiveOrZero
                                                  Long from,
                                              @RequestParam(required = false, defaultValue = "10") @Positive
                                                  Long size) {
        log.info("Start fetching events by different parameters");
        EventAdmin eventAdminParams = new EventAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        List<EventFullDto> events = eventService.findListByAdmin(eventAdminParams);
        log.info("Finish fetching events by different parameters");
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto adminUpdate(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        log.info("Start updating event {}", updateRequest);
        EventFullDto event = eventService.adminUpdate(eventId, updateRequest);
        log.info("Finish updating event {}", event);
        return event;
    }

}