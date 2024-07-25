package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.enums.EventStatus;
import ru.practicum.event.model.models.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.DataConflictException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapperMapstruct;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestMapperMapstruct mapper = RequestMapperMapstruct.INSTANCE;


    @Override
    @Transactional
    public ParticipationRequestDto add(Long userId, Long eventId) {
        User requester = verifyUser(userId);

        Event event = verifyEvent(eventId);

        List<Request> requests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);

        if (!requests.isEmpty()) {
            throw new DataConflictException(User.class, String.valueOf(userId),
                    "Заявка на участие пользователя с id " + userId + " в событии с id " + eventId + " уже существует");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new DataConflictException(User.class, String.valueOf(userId),
                    "Пользователь с id = " + userId + " является инициатором события");
        }

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new DataConflictException(EventStatus.class, String.valueOf(eventId),
                    "Данное событие ещё не опубликовано");
        }

        Long confirmedRequests = Long.valueOf(eventRepository.countConfirmedRequestsByEventId(eventId));

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(confirmedRequests)) {
            throw new DataConflictException(Event.class, String.valueOf(eventId), "Превышен лимит участников события");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        Request result = requestRepository.save(request);

        return mapper.modelToDto(result);
    }

    @Override
    public List<ParticipationRequestDto> findUserRequests(Long userId) {
        verifyUser(userId);

        List<Request> result = requestRepository.findAllByRequesterId(userId);

        return mapper.modelListToDto(result);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        verifyUser(userId);

        Request request = verifyRequest(requestId);
        request.setStatus(RequestStatus.CANCELED);

        return mapper.modelToDto(requestRepository.save(request));
    }


    private User verifyUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователя с id = " + userId + " не существует"));
    }

    private Event verifyEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "События с id = " + eventId + " не существует"));
    }

    private Request verifyRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(Request.class, String.valueOf(requestId),
                        "Запроса с id = " + requestId + " не существует"));
    }

}