package ru.practicum.event;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    // Получение подробной информации об опубликованном событии по его идентификатору
    EventFullDto getEventById(long eventId) throws JsonProcessingException;

    // Получение событий с возможностью фильтрации
    List<EventShortDto> getAllEvents(String text,
                                     List<Integer> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     boolean onlyAvailable,
                                     String sort,
                                     int from,
                                     int size) throws JsonProcessingException;

    // Получение событий, добавленных текущим пользователем
    List<EventShortDto> getEventsByUserId(long userId, int from, int size) throws JsonProcessingException;

    // Добавление нового события пользователем
    EventFullDto createEvent(long userId, EventNewDto eventNewDto);

    // Получение полной информации о событии добавленном текущим пользователем
    EventFullDto getOwnFullEventByUserId(long userId, long eventId) throws JsonProcessingException;

    // Отмена события добавленного текущим пользователем.
    EventFullDto changeEventByOwnerUser(long userId, long eventId,
                                        EventUpdateAdminDto eventUpdateAdminDto);


    // Admin: События
    // Поиск событий
    List<EventFullDto> getAllEventsFull(List<Long> usersId,
                                        List<String> states,
                                        List<Integer> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        int from,
                                        int size) throws JsonProcessingException;

    // Редактирование события
    EventFullDto changeEvent(long eventId, EventUpdateAdminDto eventUpdateAdminDto);

    List<EventFullDto> getEventsById(List<Long> ids) throws JsonProcessingException;

}
