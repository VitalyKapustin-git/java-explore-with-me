package ru.practicum.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.lang.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.http.StatsHttpClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class EventController {

    private EventService eventService;

    private HttpServletRequest request;

    private final StatsHttpClient statsHttpClient;

    // Public: События
    // Получение событий с возможностью фильтрации
    @GetMapping("/events")
    public List<EventShortDto> getAllEvents(@RequestParam(required = false) @Nullable String text,
                                            @RequestParam(required = false) @Nullable List<Integer> categories,
                                            @RequestParam(required = false) @Nullable Boolean paid,

                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            @RequestParam(required = false) @Nullable LocalDateTime rangeStart,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            @RequestParam(required = false) @Nullable LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(required = false) @Nullable String sort,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) throws JsonProcessingException {

        statsHttpClient.saveView(request.getRemoteAddr(), request.getRequestURI());

        return eventService.getAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    // Получение подробной информации об опубликованном событии по его идентификатору
    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable long eventId) throws JsonProcessingException {

        statsHttpClient.saveView(request.getRemoteAddr(), request.getRequestURI());

        return eventService.getEventById(eventId);
    }


    // Private: События
    // Получение событий, добавленных текущим пользователем
    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsByUserId(@PathVariable long userId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) throws JsonProcessingException {
        return eventService.getEventsByUserId(userId, from, size);
    }

    // Добавление нового события
    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable long userId, @Valid @RequestBody EventNewDto eventNewDto) {
        return eventService.createEvent(userId, eventNewDto);
    }

    // Получение полной информации о событии добавленном текущим пользователем
    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getOwnFullEventByUserId(@PathVariable long userId, @PathVariable long eventId) throws JsonProcessingException {
        return eventService.getOwnFullEventByUserId(userId, eventId);
    }

    // Изменение события добавленного текущим пользователем
    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto changeEventByOwnerUser(@PathVariable long userId, @PathVariable long eventId,
                                               @RequestBody EventUpdateAdminDto eventUpdateAdminDto) {
        return eventService.changeEventByOwnerUser(userId, eventId, eventUpdateAdminDto);
    }


    // Admin: События
    // Поиск событий
    @GetMapping("/admin/events")
    public List<EventFullDto> getAllEventsFull(@RequestParam(required = false) List<Long> usersId,
                                               @RequestParam(required = false) List<String> states,
                                               @RequestParam(required = false) List<Integer> categories,

                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               @RequestParam(required = false) LocalDateTime rangeStart,

                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               @RequestParam(required = false) LocalDateTime rangeEnd,

                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) throws JsonProcessingException {
        return eventService.getAllEventsFull(usersId, states, categories, rangeStart, rangeEnd, from, size);
    }

    // Редактирование события
    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto changeEvent(@PathVariable long eventId, @RequestBody EventUpdateAdminDto eventUpdateAdminDto) {
        return eventService.changeEvent(eventId, eventUpdateAdminDto);
    }

}
