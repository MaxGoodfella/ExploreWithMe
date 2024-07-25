package ru.practicum.request.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.util.List;

@Mapper
public interface RequestMapperMapstruct {

    RequestMapperMapstruct INSTANCE = Mappers.getMapper(RequestMapperMapstruct.class);

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto modelToDto(Request request);

    List<ParticipationRequestDto> modelListToDto(List<Request> requests);

}
