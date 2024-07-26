package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.enums.EventStatus;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;


@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    private Long id;

    private String annotation;

    private String description;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalDateTime createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration = true;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalDateTime publishedOn;

    private EventStatus state;

    private String title;

    private Long views;

}