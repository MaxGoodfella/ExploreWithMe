package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.StatsDto;
import ru.practicum.StatsViewDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapperMapstruct;
import ru.practicum.event.mapper.LocationMapperMapstruct;
import ru.practicum.event.model.enums.EventAdminState;
import ru.practicum.event.model.enums.EventSort;
import ru.practicum.event.model.enums.EventStatus;
import ru.practicum.event.model.enums.EventUserState;
import ru.practicum.event.model.models.Event;
import ru.practicum.event.model.models.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.DataConflictException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapperMapstruct;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final StatsClient statsClient;

    private final EventMapperMapstruct eventMapper = EventMapperMapstruct.INSTANCE;

    private final LocationMapperMapstruct locationMapper = LocationMapperMapstruct.INSTANCE;

    private final RequestMapperMapstruct requestMapper = RequestMapperMapstruct.INSTANCE;

    @Value("${server.application.name:ewm-service}")
    private String applicationName;


    @Override
    public List<EventFullDto> findListByAdmin(List<Long> users,
                                              List<EventStatus> states,
                                              List<Long> categories,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Long from,
                                              Long size) {

        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());

        List<Event> events;

        if (users == null) {
            users = userRepository.findAllId();
        }
        if (categories == null) {
            categories = categoryRepository.findAllId();
        }
        if (states == null) {
            states = List.of(EventStatus.PUBLISHED, EventStatus.CANCELED, EventStatus.PENDING);
        }

        if (rangeStart != null && rangeEnd != null) {
            events = eventRepository.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(users, states,
                            categories, rangeStart, rangeEnd, pageable).getContent();
        } else if (rangeStart != null) {
            events = eventRepository.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsAfter(users, states,
                            categories, rangeStart, pageable).getContent();
        } else if (rangeEnd != null) {
            events = eventRepository.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsBefore(users, states,
                            categories, rangeEnd, pageable).getContent();
        } else {
            events = eventRepository.findAllByInitiatorIdInAndStateInAndCategoryIdIn(users, states,
                    categories, pageable).getContent();
        }

        List<EventFullDto> result = eventMapper.modelListToFullDto(events);

        result = result.stream()
                .peek(event -> event.setConfirmedRequests(getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());

        return result;

    }

    @Override
    @Transactional
    public EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest updateEventAdmin) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "Событие с id = " + eventId + " не найдено."));

        LocalDateTime newDate = updateEventAdmin.getEventDate();
        if (newDate != null) {
            if (newDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException(Event.class, String.valueOf(eventId),
                        "Время начала изменяемого события должно быть не ранее, чем за 1 час");
            } else {
                event.setEventDate(newDate);
            }
        }

        if (updateEventAdmin.getAnnotation() != null && !updateEventAdmin.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventAdmin.getAnnotation());
        }

        if (updateEventAdmin.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdmin.getCategory()).orElseThrow(
                    () -> new EntityNotFoundException(Category.class, String.valueOf(updateEventAdmin.getCategory()),
                            "Категория с id = " + updateEventAdmin.getCategory() + " не найдена."));
            event.setCategory(category);
        }

        if (updateEventAdmin.getDescription() != null && !updateEventAdmin.getDescription().isBlank()) {
            event.setDescription(updateEventAdmin.getDescription());
        }

        if (updateEventAdmin.getLocation() != null) {
            Location location = locationRepository.save(locationMapper.toModel(updateEventAdmin.getLocation()));
            event.setLocation(location);
        }

        if (updateEventAdmin.getPaid() != null) {
            event.setPaid(updateEventAdmin.getPaid());
        }

        if (updateEventAdmin.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdmin.getParticipantLimit());
        }

        if (updateEventAdmin.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdmin.getRequestModeration());
        }

        if (updateEventAdmin.getTitle() != null && !updateEventAdmin.getTitle().isBlank()) {
            event.setTitle(updateEventAdmin.getTitle());
        }

        EventAdminState stateAction = updateEventAdmin.getStateAction();
        if (stateAction != null) {
            if (stateAction == EventAdminState.PUBLISH_EVENT) {
                if (event.getState() == EventStatus.PENDING) {
                    event.setState(EventStatus.PUBLISHED);
                    event.setPublished(LocalDateTime.now());
                } else {
                    throw new DataConflictException(Event.class, String.valueOf(eventId),
                            "Событие должно иметь статус PENDING");
                }
            } else {
                if (event.getState() == EventStatus.PUBLISHED) {
                    throw new DataConflictException(Event.class, String.valueOf(eventId),
                            "Событие должно иметь статус PENDING или CANCELED");
                } else {
                    event.setState(EventStatus.CANCELED);
                }
            }
        }

        EventFullDto result = eventMapper.toEventFullDto(eventRepository.save(event));

        result.setConfirmedRequests(getConfirmedRequests(result.getId()));

        return result;

    }

    @Override
    public List<EventShortDto> findUserEvents(Long userId, Long from, Long size) {
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();

        return eventMapper.modelListToEventShortDto(events);
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, EventNewDto eventNewDto) {

        if (eventNewDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(Event.class, String.valueOf(userId),
                    "Поле должно содержать дату, которая еще не наступила.");
        }

        if (eventNewDto.getPaid() == null) {
            eventNewDto.setPaid(false);
        }

        if (eventNewDto.getParticipantLimit() == null) {
            eventNewDto.setParticipantLimit(0L);
        }

        if (eventNewDto.getRequestModeration() == null) {
            eventNewDto.setRequestModeration(true);
        }

        User initiator = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не найден."));

        Event event = eventMapper.toEvent(eventNewDto);
        locationRepository.save(event.getLocation());
        event.setCreated(LocalDateTime.now());
        event.setInitiator(initiator);
        event.setState(EventStatus.PENDING);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findUserEventById(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не найден."));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "Событие с id = " + eventId + " не найдено."));

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto userUpdate(Long userId, Long eventId, UpdateEventUserRequest updateEventUser) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не найден."));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "Событие с id = " + eventId + " не найдено."));

        if (event.getState() == EventStatus.PUBLISHED) {
            throw new DataConflictException(Event.class, String.valueOf(eventId),
                    "Изменяемое событие должно иметь статус PENDING или CANCELED");
        }

        LocalDateTime updatedEventDate = updateEventUser.getEventDate();
        if (updatedEventDate != null) {
            if (updatedEventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException(Event.class, String.valueOf(eventId),
                        "Дата и время на которые намечено событие не может быть раньше, чем через два часа");
            } else {
                event.setEventDate(updatedEventDate);
            }
        }

        if (updateEventUser.getAnnotation() != null && !updateEventUser.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventUser.getAnnotation());
        }

        if (updateEventUser.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUser.getCategory()).orElseThrow(
                    () -> new EntityNotFoundException(Category.class, String.valueOf(updateEventUser.getCategory()),
                            "Категория с id = " + updateEventUser.getCategory() + " не найдена."));
            event.setCategory(category);
        }

        if (updateEventUser.getDescription() != null && !updateEventUser.getDescription().isBlank()) {
            event.setDescription(updateEventUser.getDescription());
        }

        if (updateEventUser.getLocation() != null) {
            Location location = locationRepository.save(locationMapper.toModel(updateEventUser.getLocation()));
            event.setLocation(location);
        }

        if (updateEventUser.getPaid() != null) {
            event.setPaid(updateEventUser.getPaid());
        }

        if (updateEventUser.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUser.getParticipantLimit());
        }

        if (updateEventUser.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUser.getRequestModeration());
        }

        if (updateEventUser.getTitle() != null && !updateEventUser.getTitle().isBlank()) {
            event.setTitle(updateEventUser.getTitle());
        }

        if (updateEventUser.getStateAction() != null) {
            if (updateEventUser.getStateAction().equals(EventUserState.CANCEL_REVIEW)) {
                event.setState(EventStatus.CANCELED);
            } else {
                event.setState(EventStatus.PENDING);
            }
        }

        Event result = eventRepository.save(event);

        return eventMapper.toEventFullDto(result);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не найден."));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "Событие с id = " + eventId + " не найдено."));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new BadRequestException(User.class, String.valueOf(userId),
                    "Пользователь не является инициатором события");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requestMapper.modelListToDto(requests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatusRequests(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest requestStatusUpdate) {

        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не найден."));

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "Событие с id = " + eventId + " не найдено."));

        event.setConfirmedRequests(eventRepository.countConfirmedRequestsByEventId(eventId));

        List<Request> requests = requestRepository.findAllByIdIn(requestStatusUpdate.getRequestIds());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            for (Request request : requests) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.modelToDto(requestRepository.save(request)));
            }
        } else {
            Long requestCountToLimit = event.getParticipantLimit() - event.getConfirmedRequests();

            if (requestCountToLimit <= 0) {
                throw new DataConflictException(Event.class, String.valueOf(eventId),
                        "Достигнут лимит участников");
            }

            for (Request request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new DataConflictException(Event.class, String.valueOf(eventId),
                            "Запрос должен иметь статус PENDING");
                }

                if (requestStatusUpdate.getStatus().equals(RequestStatus.REJECTED) || requestCountToLimit == 0) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.modelToDto(requestRepository.save(request)));
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(requestMapper.modelToDto(requestRepository.save(request)));
                    requestCountToLimit--;
                }
            }
        }

        eventRepository.save(event);

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);

    }

    @Override
    public List<EventShortDto> findEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Boolean onlyAvailable,
                                          EventSort sort,
                                          Integer from,
                                          Integer size,
                                          HttpServletRequest request) {

        int page = (from / size);
        Pageable pageable = PageRequest.of(page, size);

        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException(LocalDateTime.class, "", "Некорректные параметры времени начала и/или конца");
        }

        if (categories == null) {
            categories = categoryRepository.findAllId();
        }

        if (rangeEnd == null && rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        List<Event> events = eventRepository.search(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable)
                .getContent();

        Map<Long, Long> views = getViews(events);

        List<EventShortDto> result = eventMapper.modelListToEventShortDto(events);
        result = result.stream()
                .peek(event -> event.setViews(views.getOrDefault(event.getId(), 0L)))
                .peek(event -> event.setConfirmedRequests(getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());

        sendHit(request);

        if (sort == EventSort.EVENT_DATE) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());
        } else if (sort == EventSort.VIEWS) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public EventFullDto findEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "Событие с id = " + eventId + " не найдено."));

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new EntityNotFoundException(Event.class, String.valueOf(eventId),
                    "Событие с id = " + eventId + " не опубликовано");
        }

        Map<Long, Long> views = getViews(List.of(event));

        EventFullDto result = eventMapper.toEventFullDto(event);
        result.setConfirmedRequests(getConfirmedRequests(result.getId()));
        result.setViews(views.getOrDefault(event.getId(), 0L));

        sendHit(request);

        return result;
    }


    private Map<Long, Long> getViews(List<Event> events) {
        LocalDateTime start = events.stream()
                .map(Event::getCreated)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new EntityNotFoundException(LocalDateTime.class, null, ""));

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<StatsViewDto> views = statsClient.getStatistics(start, LocalDateTime.now(), uris, true);

        Map<Long, Long> eventViews = new HashMap<>();

        for (StatsViewDto view : views) {
            if (view.getUri().equals("/events")) {
                continue;
            }
            Long eventId = Long.parseLong(view.getUri().substring("/events".length() + 1));
            eventViews.put(eventId, view.getHits());
        }

        return eventViews;
    }

    private void sendHit(HttpServletRequest request) {
        statsClient.hit(StatsDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private Integer getConfirmedRequests(Long eventId) {
        return eventRepository.countConfirmedRequestsByEventId(eventId);
    }

}