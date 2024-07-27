package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;


    @GetMapping
    public List<CommentResponseDto> getCommentsAdmin(@RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                     @RequestParam(defaultValue = "10") @Positive Long size) {
        log.info("Start fetching comments with parameters from = {} and size = {}", from, size);
        List<CommentResponseDto> fetchedComments = commentService.getCommentsAdmin(from, size);
        log.info("Finish fetching comments with parameters from = {} and size = {}", from, size);
        return fetchedComments;
    }

    @DeleteMapping(value = "/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByIdAdmin(@PathVariable(name = "commentId") Long commentId) {
        log.info("Start deleting comment by id: {}", commentId);
        commentService.deleteByIdAdmin(commentId);
    }

}