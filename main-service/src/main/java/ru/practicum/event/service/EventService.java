package ru.practicum.event.service;

import ru.practicum.event.dto.EventAdmin;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUser;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface EventService {

    EventFullDto adminUpdate(Long eventId,
                             UpdateEventAdminRequest eventUpdate);

    List<EventFullDto> findListByAdmin(EventAdmin eventAdminParams);

    EventFullDto addEvent(Long userId, EventNewDto newEventDto);

    List<EventShortDto> findUserEvents(Long userId, Long from, Long size);

    EventFullDto findUserEventById(Long userId, Long eventId);

    EventFullDto userUpdate(Long userId, Long eventId, UpdateEventUserRequest eventUpdate);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequests(Long userId,
                                                        Long eventId,
                                                        EventRequestStatusUpdateRequest requestStatusUpdateRequest);

    List<EventShortDto> findEvents(EventUser eventUserParams, HttpServletRequest request);

    EventFullDto findEventById(Long eventId, HttpServletRequest request);

}