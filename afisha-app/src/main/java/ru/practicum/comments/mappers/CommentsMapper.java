package ru.practicum.comments.mappers;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentNewDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.user.mapper.UserMapper;

public class CommentsMapper {

    public static Comment toComment(CommentNewDto commentNewDto) {

        Comment comment = new Comment();
        comment.setComment(commentNewDto.getComment());

        return comment;

    }

    public static CommentShortDto toCommentShortDto(Comment comment) {

        CommentShortDto commentShortDto = new CommentShortDto();

        commentShortDto.setId(comment.getId());
        commentShortDto.setAuthor(comment.getAuthor().getId());
        commentShortDto.setEvent(comment.getEvent().getId());
        commentShortDto.setComment(comment.getComment());

        return commentShortDto;

    }

    public static CommentDto toCommentDto(Comment comment) {

        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setAuthor(UserMapper.toUserDto(comment.getAuthor()));
        commentDto.setEvent(EventMapper.toEventShortDto(comment.getEvent()));
        commentDto.setComment(comment.getComment());
        if (comment.getUpdated() != null) commentDto.setUpdated(comment.getUpdated());

        return commentDto;

    }

}
