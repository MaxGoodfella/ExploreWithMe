package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    @NotNull
    private Long id;

    @NotBlank
    @Length(max = 250, min = 1)
    private String text;

    @NotNull
    private String author;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String created;

}