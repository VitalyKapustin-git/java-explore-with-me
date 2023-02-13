package ru.practicum.request.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestShortDto;
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class RequestController {

    private RequestService requestService;

    // Private: Запросы на участие
    // Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping("/users/{userId}/requests")
    public List<RequestShortDto> userRequests(@PathVariable long userId) {
        return requestService.userRequests(userId);
    }

    // Добавление запроса от текущего пользователя на участие в событии
    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestShortDto createUserRequest(@PathVariable long userId, @RequestParam long eventId) throws JsonProcessingException {
        return requestService.createUserRequest(userId, eventId);
    }

    // Отмена своего запроса на участие в событии
    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestShortDto revokeOwnRequest(@PathVariable long userId, @PathVariable long requestId) {
        return requestService.revokeOwnRequest(userId, requestId);
    }


    // Private: События
    // Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestShortDto> getEventRequestsForUser(@PathVariable long userId, @PathVariable long eventId) {
        return requestService.getEventRequestsForUser(userId, eventId);
    }

    // Новый статус для заявок на участие в событии текущего пользователя
    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatusForOwnEvent(@PathVariable long userId,
                                                                         @PathVariable long eventId,
                                                                         @RequestBody @Valid RequestStatusDto requestStatusDto) throws JsonProcessingException {
        return requestService.changeRequestStatusForOwnEvent(userId, eventId, requestStatusDto);
    }

}
