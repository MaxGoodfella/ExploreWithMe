package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class PublicCommentController {

    private final CommentService commentService;


    @GetMapping
    public List<CommentResponseDto> getCommentsPublic(@RequestParam(name = "eventId", required = false) Long eventId,
                                                      @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
                                                      @RequestParam(required = false, defaultValue = "10") @Positive Long size) {
        log.info("Start fetching comments for event with id = {} with parameters from = {} and size = {}",
                eventId, from, size);
        List<CommentResponseDto> fetchedComments = commentService.getCommentsPublic(eventId, from, size);
        log.info("Finish fetching comments for event with id = {} with parameters from = {} and size = {}",
                eventId, from, size);
        return fetchedComments;
    }

    @GetMapping(value = "/{commentId}")
    public CommentResponseDto findById(@PathVariable(name = "commentId") Long commentId) {
        log.info("Start fetching comment with id = {}", commentId);
        CommentResponseDto fetchedComment = commentService.findById(commentId);
        log.info("Finish fetching comment with id = {}", commentId);
        return fetchedComment;
    }

}