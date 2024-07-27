package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.models.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.DataConflictException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CommentResponseDto save(Long userId, Long eventId, CommentRequestDto commentRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не найден."));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "Событие с id = " + eventId + " не найдено."));

        LocalDateTime created = LocalDateTime.now();

        String text = commentRequestDto.getText();

        if (text.isBlank() || text.isEmpty()) {
            throw new BadRequestException(CommentRequestDto.class, text, "Пустое поле текста комментария");
        }

        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(created);
        comment.setText(text);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentResponseDto(savedComment);
    }

    @Override
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не найден."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(Comment.class, String.valueOf(commentId),
                        "Комментарий с id = " + commentId + " не найден."));

        if (!Objects.equals(userId, comment.getAuthor().getId())) {
            throw new DataConflictException(User.class, String.valueOf(userId), "Только автор может редактировать комментарий");
        }

        if (!commentRequestDto.getText().isBlank()) {
            comment.setText(commentRequestDto.getText());
        }

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentResponseDto(savedComment);
    }

    @Override
    public List<CommentResponseDto> getCommentsPublic(Long eventId, Long from, Long size) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(Event.class, String.valueOf(eventId),
                        "Событие с id = " + eventId + " не найдено."));

        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, Math.toIntExact(size));

        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageable).getContent();

        return CommentMapper.toCommentResponseDtos(comments);
    }

    @Override
    public CommentResponseDto findById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(Comment.class, String.valueOf(commentId),
                        "Комментарий с id = " + commentId + " не найден."));
        return CommentMapper.toCommentResponseDto(comment);
    }

    @Override
    public List<CommentResponseDto> getCommentsAdmin(Long from, Long size) {
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, Math.toIntExact(size));

        List<Comment> comments = commentRepository.findAll(pageable).getContent();

        return CommentMapper.toCommentResponseDtos(comments);
    }

    @Override
    @Transactional
    public void deleteByIdAdmin(Long commentId) {
        commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(Comment.class, String.valueOf(commentId),
                        "Комментарий с id = " + commentId + " не найден."));
        commentRepository.deleteById(commentId);
    }

}