package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    @NotBlank
    @Length(max = 250, min = 1)
    private String text;

}