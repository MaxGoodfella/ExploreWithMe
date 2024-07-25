package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.models.Event;

import java.util.List;


@Mapper
public interface EventMapperMapstruct {

    EventMapperMapstruct INSTANCE = Mappers.getMapper(EventMapperMapstruct.class);


    @Mapping(target = "category.id", source = "newEventDto.category")
    Event toEvent(EventNewDto newEventDto);

    @Mapping(source = "state", target = "state")
    EventFullDto toEventFullDto(Event event);

    @Mapping(source = "event.id", target = "id")
    @Mapping(source = "event.annotation", target = "annotation")
    @Mapping(source = "event.category", target = "category")
    @Mapping(source = "event.confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "event.eventDate", target = "eventDate")
    @Mapping(source = "event.initiator", target = "initiator")
    @Mapping(source = "event.paid", target = "paid")
    @Mapping(source = "event.title", target = "title")
    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> modelListToEventShortDto(List<Event> events);

    List<EventFullDto> modelListToFullDto(List<Event> events);

}
