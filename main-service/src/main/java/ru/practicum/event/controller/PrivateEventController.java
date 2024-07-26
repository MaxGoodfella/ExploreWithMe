package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody EventNewDto eventNewDto) {
        log.info("Start saving event {}", eventNewDto);
        EventFullDto eventFullDto = eventService.addEvent(userId, eventNewDto);
        log.info("Finish saving event {}", eventNewDto);
        return eventFullDto;
    }

    @GetMapping
    public List<EventShortDto> findUserEvents(@PathVariable Long userId,
                                              @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
                                              @RequestParam(required = false, defaultValue = "10") @Positive Long size) {
        log.info("Start fetching events for user with id = {} and with search parameters from = {} and size = {}",
                userId, from, size);
        List<EventShortDto> eventShortDtoList = eventService.findUserEvents(userId, from, size);
        log.info("Finish fetching events for user with id = {} and with search parameters from = {} and size = {}",
                userId, from, size);
        return eventShortDtoList;
    }

    @GetMapping("/{eventId}")
    public EventFullDto findUserEventById(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        log.info("Start fetching event with id = {} for user with id = {}", eventId, userId);
        EventFullDto eventFullDto = eventService.findUserEventById(userId, eventId);
        log.info("Finish fetching event with id = {} for user with id = {}", eventId, userId);
        return eventFullDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto userUpdate(@PathVariable Long userId,
                                   @PathVariable Long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Start updating event with id = {}", userId);
        EventFullDto eventFullDto = eventService.userUpdate(userId, eventId, updateEventUserRequest);
        log.info("Finish updating event {}", updateEventUserRequest);
        return eventFullDto;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Start fetching requests from user with id = {} to event with id = {}", eventId, userId);
        List<ParticipationRequestDto> fetchedRequests = eventService.getRequests(userId, eventId);
        log.info("Finish fetching requests from user with id = {} to event with id = {}", eventId, userId);
        return fetchedRequests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequests(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Start updating event with id = {} by user with id = {}", eventId, userId);
        EventRequestStatusUpdateResult updated = eventService.updateStatusRequests(userId, eventId, eventRequestStatusUpdateRequest);
        log.info("Finish updating event with id = {} by user with id = {}", eventId, userId);
        return updated;
    }

}