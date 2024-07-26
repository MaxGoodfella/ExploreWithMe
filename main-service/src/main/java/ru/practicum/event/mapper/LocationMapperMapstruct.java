package ru.practicum.event.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.models.Location;

@Mapper
public interface LocationMapperMapstruct {

    LocationMapperMapstruct INSTANCE = Mappers.getMapper(LocationMapperMapstruct.class);

    Location toModel(LocationDto locationDto);

}
