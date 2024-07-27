package ru.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.model.Comment;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@UtilityClass
public class CommentMapper {

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    public static List<CommentResponseDto> toCommentResponseDtos(Iterable<Comment> comments) {
        List<CommentResponseDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(toCommentResponseDto(comment));
        }
        return dtos;
    }

}