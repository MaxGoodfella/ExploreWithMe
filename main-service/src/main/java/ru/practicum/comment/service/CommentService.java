package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;

import java.util.List;


public interface CommentService {

    CommentResponseDto save(Long userId, Long eventId, CommentRequestDto commentRequestDto);

    CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto);

    List<CommentResponseDto> getCommentsPublic(Long eventId, Long from, Long size);

    CommentResponseDto findById(Long commentId);

    List<CommentResponseDto> getCommentsAdmin(Long from, Long size);

    void deleteByIdAdmin(Long commentId);

}