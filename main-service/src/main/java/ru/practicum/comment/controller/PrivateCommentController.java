package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.NotNull;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto save(@NotNull @PathVariable(name = "userId") Long userId,
                                   @NotNull @RequestParam(name = "eventId") Long eventId,
                                   @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Start saving comment for user {} with event id {}", userId, eventId);
        CommentResponseDto savedComment = commentService.save(userId, eventId, commentRequestDto);
        log.info("Finish saving comment for user {} with event id {}", userId, eventId);
        return savedComment;
    }

    @PatchMapping(value = "/{commentId}")
    public CommentResponseDto updateComment(@NotNull @PathVariable(name = "userId") Long userId,
                                            @NotNull @PathVariable(name = "commentId") Long commentId,
                                            @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Start updating comment with id {} from user {}", commentId, userId);
        CommentResponseDto updatedComment = commentService.updateComment(userId, commentId, commentRequestDto);
        log.info("Finish updating comment with id {} from user {}", updatedComment.getId(), userId);
        return updatedComment;
    }

}