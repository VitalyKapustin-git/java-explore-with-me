package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentNewDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    CommentShortDto createComment(Long userId, Long eventId, CommentNewDto commentNewDto);

    List<CommentShortDto> getEventComments(Long eventId, int from, int size);

    void removeOwnComment(Long authorId, Long eventId);

    void removeCommentAdmin(Long commentId);

    CommentDto updateComment(Long userId, Long eventId, CommentUpdateDto commentUpdateDto);

    List<CommentDto> getUserComments(Long userId, int from, int size);

    List<CommentDto> getUserCommentForEvent(Long userId, Long eventId);

    CommentDto updateCommentAdmin(CommentUpdateDto commentUpdateDto);

    CommentShortDto getComment(Long commentId);
}
