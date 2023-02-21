package ru.practicum.comments.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dao.CommentsRepository;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentNewDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.CommentUpdateDto;
import ru.practicum.comments.mappers.CommentsMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.core.exceptions.NotFoundException;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> getEventComments(Long eventId, int from, int size) {

        Pageable pageable = PageRequest.of(from / size, size);
        validateEvent(eventId);

        return commentsRepository.getCommentsByEvent_Id(eventId, pageable).stream()
                .map(CommentsMapper::toCommentShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentShortDto getComment(Long commentId) {

        Comment comment = commentsRepository.getCommentById(commentId);

        if (comment == null) throw new NotFoundException("Comment not found");

        return CommentsMapper.toCommentShortDto(comment);

    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserCommentForEvent(Long userId, Long eventId) {

        validateUser(userId);
        validateEvent(eventId);

        return commentsRepository.getCommentsByAuthor_IdAndEvent_Id(userId, eventId).stream()
                .map(CommentsMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(Long userId, int from, int size) {

        Pageable pageable = PageRequest.of(from / size, size);
        validateUser(userId);

        return commentsRepository.getCommentsByAuthor_Id(userId, pageable).stream()
                .map(CommentsMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentShortDto createComment(Long userId, Long eventId, CommentNewDto commentNewDto) {

        Comment comment = CommentsMapper.toComment(commentNewDto);
        comment.setAuthor(validateUser(userId));
        comment.setEvent(validateEvent(eventId));

        return CommentsMapper.toCommentShortDto(commentsRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, CommentUpdateDto commentUpdateDto) {

        Long commentUpdateDtoId = commentUpdateDto.getId();
        validateUser(userId);
        validateEvent(eventId);

        Comment comment = commentsRepository.getCommentByIdAndAuthor_Id(commentUpdateDtoId, userId);
        validateComment(comment, commentUpdateDtoId);
        comment.setComment(commentUpdateDto.getComment());
        comment.setUpdated(LocalDateTime.now());

        return CommentsMapper.toCommentDto(commentsRepository.save(comment));
    }

    @Override
    @Transactional
    public void removeOwnComment(Long authorId, Long commentId) {

        if (commentsRepository.existsCommentByIdAndAndAuthor_Id(commentId, authorId) == null)
            throw new NotFoundException("Comment with id=" + commentId + " not found.");
        commentsRepository.removeCommentByIdAndAuthor_Id(commentId, authorId);

    }

    @Override
    @Transactional
    public void removeCommentAdmin(Long commentId) {
        if (commentsRepository.getCommentById(commentId) == null)
            throw new NotFoundException("Comment with id=" + commentId + " not found.");
        commentsRepository.removeCommentById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateCommentAdmin(CommentUpdateDto commentUpdateDto) {

        Comment comment = commentsRepository.getCommentById(commentUpdateDto.getId());
        comment.setComment(commentUpdateDto.getComment());
        comment.setUpdated(LocalDateTime.now());

        return CommentsMapper.toCommentDto(comment);
    }

    private void validateComment(Comment comment, Long commentId) {
        if (comment == null) throw new NotFoundException("Comment with id=" + commentId + " not found.");
    }

    private User validateUser(Long userId) {

        User user = userRepository.getUserById(userId);

        if (user == null) throw new NotFoundException("Not found user with id=" + userId);

        return user;

    }

    private Event validateEvent(Long eventId) {

        Event event = eventRepository.getEventById(eventId);

        if (event == null) throw new NotFoundException("Not found event with id=" + eventId);

        return event;

    }

}
