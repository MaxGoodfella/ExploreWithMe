package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsViewDto {

    @NotEmpty
    private String app;

    @NotEmpty
    private String uri;

    private long hits;

}
