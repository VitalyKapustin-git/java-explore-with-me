package ru.practicum.request;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface RequestService {

    List<RequestShortDto> userRequests(long userId);

    RequestShortDto createUserRequest(long userId, long eventId) throws JsonProcessingException;

    RequestShortDto revokeOwnRequest(long userId, long requestId);

    // Private: События
    // Получение информации о запросах на участие в событии текущего пользователя
    List<RequestShortDto> getEventRequestsForUser(long userId, long eventId);

    // Подтверждение чужой заявки на участие в событии текущего пользователя
    EventRequestStatusUpdateResult changeRequestStatusForOwnEvent(long userId,
                                                   long eventId,
                                                   RequestStatusDto requestStatusDto)
            throws JsonProcessingException;

}
