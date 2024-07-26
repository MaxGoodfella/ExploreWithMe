package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable Long userId,
                                       @NotNull
                                       @RequestParam Long eventId) {
        log.info("Start saving request for event id = {} from user with id = {}", eventId, userId);
        ParticipationRequestDto participationRequestDto = requestService.add(userId, eventId);
        log.info("Finish saving request for event with id = {} from user with id = {}", eventId, userId);
        return participationRequestDto;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Start rejecting request with id = {} from user with id = {}", requestId, userId);
        ParticipationRequestDto participationRequestDto = requestService.cancel(userId, requestId);
        log.info("Finish rejecting request with id = {} from user with id = {}", requestId, userId);
        return participationRequestDto;
    }

    @GetMapping
    public List<ParticipationRequestDto> findUserRequests(@PathVariable Long userId) {
        log.info("Start fetching requests from user with id = {}", userId);
        List<ParticipationRequestDto> fetchedRequests = requestService.findUserRequests(userId);
        log.info("Finish fetching requests from user with id = {}", userId);
        return fetchedRequests;
    }

}