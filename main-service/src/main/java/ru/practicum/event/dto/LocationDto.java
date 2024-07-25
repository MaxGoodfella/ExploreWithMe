package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {

    @Min(-90)
    @Max(90)
    private Float lat;

    @Min(-180)
    @Max(180)
    private Float lon;

}