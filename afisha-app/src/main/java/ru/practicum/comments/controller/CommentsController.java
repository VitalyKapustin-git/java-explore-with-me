package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentNewDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.CommentUpdateDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@AllArgsConstructor
public class CommentsController {

    private CommentService commentService;

    // Public
    // Список всех комментариев для события
    @GetMapping("/event/{eventId}/comments")
    public List<CommentShortDto> getEventComments(@PathVariable Long eventId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        return commentService.getEventComments(eventId, from, size);
    }

    // Получение комментария по id
    @GetMapping("/comments/{commentId}")
    public CommentShortDto getComment(@PathVariable Long commentId) {
        return commentService.getComment(commentId);
    }


    // Private
    // Комментарии пользователя для конкретного событии
    @GetMapping("/user/{userId}/event/{eventId}/comments")
    public List<CommentDto> getUserCommentForEvent(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        return commentService.getUserCommentForEvent(userId, eventId);
    }

    // Все комментарии пользователя
    @GetMapping("/user/{userId}/comments")
    public List<CommentDto> getUserComments(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        return commentService.getUserComments(userId, from, size);
    }

    // Создание комментария пользователем
    @PostMapping("/user/{userId}/event/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody CommentNewDto commentNewDto) {
        return commentService.createComment(userId, eventId, commentNewDto);
    }

    // Правка собственного комментария
    @PatchMapping("/user/{userId}/event/{eventId}/comments")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody CommentUpdateDto commentUpdateDto) {
        return commentService.updateComment(userId, eventId, commentUpdateDto);
    }

    // Удаление собственного комментария для события
    @DeleteMapping("/user/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOwnComment(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.removeOwnComment(userId, commentId);
    }


    // Admin
    // Удаление любого комментария
    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCommentAdmin(@PathVariable Long commentId) {
        commentService.removeCommentAdmin(commentId);
    }

    // Редактирование любого комментария
    @PatchMapping("/admin/comments")
    public CommentDto updateCommentAdmin(@RequestBody CommentUpdateDto commentUpdateDto) {
        return commentService.updateCommentAdmin(commentUpdateDto);
    }

}
