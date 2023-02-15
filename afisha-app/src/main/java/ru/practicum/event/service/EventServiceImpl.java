package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.service.CategoryService;
import ru.practicum.core.exceptions.ConflictException;
import ru.practicum.core.exceptions.NotFoundException;
import ru.practicum.core.http.StatsHttpClient;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ConfirmedRequestsDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.service.UserService;
import ru.practicum.view.dto.StatsDto;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserService userService;

    private final CategoryService categoryService;

    private final StatsHttpClient statsHttpClient;

    private final RequestRepository requestRepository;


    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long eventId) {

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.getEventById(eventId));


        Long eventViews = this.getViews(statsHttpClient.getViews("/events/" + eventId, eventFullDto.getCreatedOn()));


        Long confirmedRequests = countConfirmedRequests(List.of(eventFullDto.getId()))
                .get(eventFullDto.getId());

        eventFullDto.setViews(eventViews);
        eventFullDto.setConfirmedRequests(confirmedRequests == null ? 0L : confirmedRequests);

        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEvents(String text,
                                            List<Integer> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            boolean onlyAvailable,
                                            String sort,
                                            int from,
                                            int size) {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        List<Event> findByCriterias = eventRepository.findAll((Specification<Event>) (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (text != null && !text.isBlank()) {
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("description")),
                                        "%" + text.toLowerCase() + "%"),
                                cb.like(cb.lower(root.get("annotation")),
                                        "%" + text.toLowerCase() + "%")
                        )
                );
            }

            if (categories != null && categories.size() > 0) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if (paid != null) {

                if (paid) {
                    predicates.add(cb.isTrue(root.get("paid")));
                }

                if (!paid) {
                    predicates.add(cb.isFalse(root.get("paid")));
                }
            }

            if (rangeStart != null && rangeEnd != null) {

                if (rangeStart.isAfter(rangeEnd)) throw new NotFoundException("Start date could not be after end date");
                predicates.add(cb.between(root.get("eventDate"), rangeStart, rangeEnd));
            }

            if (rangeStart != null && rangeEnd == null) {
                predicates.add(cb.greaterThan(root.get("eventDate"), rangeStart));
            }

            if (rangeStart == null && rangeEnd != null) {
                predicates.add(cb.lessThan(root.get("eventDate"), rangeEnd));
            }

            if (sort != null && sort.equalsIgnoreCase("EVENT_DATE")) {
                cq.orderBy(cb.desc(root.get("eventDate")));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable).getContent();

        List<EventShortDto> eventShortDtos = findByCriterias.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        addViewsToEvents(eventShortDtos);

        // Проверка, есть ли в событии еще доступные места или нет (сравнение подтвержденных заявок и кол-ва мест на соб)
        if (onlyAvailable) eventShortDtos = eventShortDtos.stream()
                .filter(v -> Optional.ofNullable(v.getConfirmedRequests()).orElse(0L) == 0 ||
                        Optional.ofNullable(v.getConfirmedRequests()).orElse(0L) <
                                Optional.ofNullable(v.getParticipantLimit()).orElse(0L))
                .collect(Collectors.toList());

        return (sort != null && sort.equalsIgnoreCase("VIEWS")) ?
                eventShortDtos.stream()
                        .sorted((v1, v2) -> v1.getViews() < v2.getViews() ? 0 : 1)
                        .collect(Collectors.toList()) : eventShortDtos;

    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUserId(long userId, int from, int size) {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        List<EventShortDto> eventShortDtos = eventRepository.getEventsByInitiatorId(userId, pageable).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        addViewsToEvents(eventShortDtos);

        return eventShortDtos;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, EventNewDto eventNewDto) {

        if (eventNewDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
            throw new ConflictException("Event date must be in future and +2 hours from actual time");

        eventNewDto.setCategoryDto(categoryService.getCategory(eventNewDto.getCategory()));

        Event event = EventMapper.toEvent(eventNewDto);
        event.setInitiator(UserMapper.toUser(userService.getUsers(List.of(userId), 0, 1).get(0)));
        event.setState(EventState.PENDING.toString());

        return EventMapper.toEventFullDto(eventRepository.save(event));

    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getOwnFullEventByUserId(long userId, long eventId) {

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.getEventByIdAndInitiator_Id(eventId, userId));
        Long confirmedRequests = countConfirmedRequests(List.of(eventFullDto.getId()))
                .get(eventFullDto.getId());

        eventFullDto.setViews(this.getViews(statsHttpClient.getViews("/events/" + eventFullDto.getId(),
                eventFullDto.getCreatedOn())
        ));
        eventFullDto.setConfirmedRequests(confirmedRequests == null ? 0L : confirmedRequests);

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto changeEventByOwnerUser(long userId, long eventId,
                                               EventUpdateAdminDto eventUpdateAdminDto) {

        LocalDateTime newEventTime = eventUpdateAdminDto.getEventDate();
        if (newEventTime != null) {
            if (eventUpdateAdminDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
                throw new ConflictException("Event date must be in future and +2 hours from actual time");
        }

        Event oldEvent = eventRepository.getEventById(eventId);
        if (oldEvent == null) throw new NotFoundException("Not found event with id=" + eventId);
        if (oldEvent.getInitiator().getId() != userId) throw new ConflictException("You are not event owner");
        if (oldEvent.getState().equals("PUBLISHED")) throw new ConflictException("Can't edit PUBLISHED event");

        extractEvent(oldEvent, eventUpdateAdminDto);

        return EventMapper.toEventFullDto(eventRepository.save(oldEvent));
    }


    // Admin: События
    // Поиск событий
    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventsFull(List<Long> users,
                                               List<String> states,
                                               List<Integer> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               int from,
                                               int size) {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        // Некорректная обработка в тестах postman. Ранее был только EventState = PUBLISHED
        // с февраля похоже заменилось на PUBLISH_EVENT

        List<Event> findByCriterias = eventRepository.findAll((Specification<Event>) (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (users != null && users.size() > 0) {
                predicates.add(root.get("initiator").get("id").in(users));
            }

            if (states != null && states.size() > 0) {
                predicates.add(root.get("state").in(states));
            }

            if (categories != null && categories.size() > 0) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if (rangeStart != null && rangeEnd != null) {

                if (rangeStart.isAfter(rangeEnd)) throw new NotFoundException("Start date could not be after end date");
                predicates.add(cb.between(root.get("eventDate"), rangeStart, rangeEnd));
            }

            if (rangeStart != null && rangeEnd == null) {
                predicates.add(cb.greaterThan(root.get("eventDate"), rangeStart));
            }

            if (rangeStart == null && rangeEnd != null) {
                predicates.add(cb.lessThan(root.get("eventDate"), rangeEnd));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable).getContent();

        List<EventFullDto> eventFullDtos = findByCriterias.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        addViewsToEvents(eventFullDtos);

        return eventFullDtos;

    }

    // Редактирование события
    @Override
    @Transactional
    public EventFullDto changeEvent(long eventId,
                                    EventUpdateAdminDto eventUpdateAdminDto) {

        Event oldEvent = eventRepository.getEventById(eventId);
        validateEvent(oldEvent, eventUpdateAdminDto);
        extractEvent(oldEvent, eventUpdateAdminDto);

        return EventMapper.toEventFullDto(eventRepository.save(oldEvent));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsById(List<Long> ids) {

        List<EventFullDto> eventFullDtos = eventRepository.getEventsByIdIn(ids).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        addViewsToEvents(eventFullDtos);

        return eventFullDtos;
    }

    private void extractEvent(Event oldEvent, EventUpdateAdminDto eventUpdateAdminDto) {

        if (eventUpdateAdminDto.getCategory() != null &&
                categoryService.getCategory(eventUpdateAdminDto.getCategory()) != null) {
            oldEvent.setCategory(CategoryMapper.toCategory(
                    categoryService.getCategory(eventUpdateAdminDto.getCategory()))
            );
        }

        if (eventUpdateAdminDto.getAnnotation() != null) oldEvent.setAnnotation(eventUpdateAdminDto.getAnnotation());
        if (!Optional.ofNullable(eventUpdateAdminDto.getDescription()).orElse("").isBlank())
            oldEvent.setDescription(eventUpdateAdminDto.getDescription());
        if (eventUpdateAdminDto.getEventDate() != null) oldEvent.setEventDate(eventUpdateAdminDto.getEventDate());
        if (eventUpdateAdminDto.getLocation() != null) {
            oldEvent.setLat(eventUpdateAdminDto.getLocation() == null ? 0.0F : eventUpdateAdminDto.getLocation().getLat());
            oldEvent.setLon(eventUpdateAdminDto.getLocation() == null ? 0.0F : eventUpdateAdminDto.getLocation().getLon());
        }
        if (eventUpdateAdminDto.getPaid() != null) oldEvent.setPaid(eventUpdateAdminDto.getPaid());
        if (eventUpdateAdminDto.getParticipantLimit() != null)
            oldEvent.setParticipantLimit(eventUpdateAdminDto.getParticipantLimit());
        if (eventUpdateAdminDto.getRequestModeration() != null)
            oldEvent.setRequestModeration(eventUpdateAdminDto.getRequestModeration());
        if (!Optional.ofNullable(eventUpdateAdminDto.getTitle()).orElse("").isBlank())
            oldEvent.setTitle(eventUpdateAdminDto.getTitle());
        if (eventUpdateAdminDto.getStateAction() != null) {
            switch (eventUpdateAdminDto.getStateAction()) {
                case "PUBLISH_EVENT":
                    oldEvent.setState(EventState.PUBLISHED.toString());
                    break;
                case "CANCEL_REVIEW":
                case "REJECT_EVENT":
                    oldEvent.setState(EventState.CANCELED.toString());
                    break;
                case "SEND_TO_REVIEW":
                    oldEvent.setState(EventState.PENDING.toString());
                    break;
                default:
                    throw new NotFoundException("Not found state for required action");
            }
        }

    }

    private void validateEvent(Event oldEvent, EventUpdateAdminDto eventUpdateAdminDto) {

        LocalDateTime newEventDate = eventUpdateAdminDto.getEventDate();
        String oldEventState = oldEvent.getState();
        String newEventState = eventUpdateAdminDto.getStateAction();

        if (newEventDate != null) {
            if (eventUpdateAdminDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
                throw new ConflictException("Event date must be in future and +2 hours from actual time");
        }

        if (newEventDate != null) {
            if (newEventDate.isBefore(LocalDateTime.now()))
                throw new ConflictException("You can't setup past date");
        }

        if (newEventState != null) {
            if (oldEventState.equals(EventState.PUBLISHED.toString())
                    && newEventState.equals("PUBLISH_EVENT"))
                throw new ConflictException("Event already published");
            if (oldEventState.equals(EventState.PUBLISHED.toString())
                    && newEventState.equals("REJECT_EVENT"))
                throw new ConflictException("Published event couldn't be cancelled");
            if (oldEventState.equals(EventState.CANCELED.toString())
                    && newEventState.equals("PUBLISH_EVENT")) {
                throw new ConflictException("Can't publish canceled event");
            }
        }
    }

    private Long getViews(List<StatsDto> statsDtoList) {
        if (statsDtoList != null && statsDtoList.size() > 0)
            return statsDtoList.get(0).getHits();
        else
            return 0L;
    }

    private void addViewsToEvents(List<? extends IEventViewsDto> events) {

        StringBuilder uris = new StringBuilder();

        Map<Long, Long> confirmedRequests = countConfirmedRequests(events.stream()
                .map(IEventViewsDto::getId)
                .collect(Collectors.toList())
        );

        events.forEach(e ->
                uris.append("/events/")
                        .append(e.getId())
                        .append(",")
        );

        uris.deleteCharAt(uris.length() - 1);

        List<LocalDateTime> eventsDate = events.stream()
                .map(IEventViewsDto::getEventDate)
                .collect(Collectors.toList());

        LocalDateTime minDate = Collections.min(eventsDate);
        List<StatsDto> statsDtoList = statsHttpClient.getViews(uris.toString(), minDate);

        Map<String, Long> views = statsDtoList.stream().collect(Collectors.toMap(
                StatsDto::getUri,
                StatsDto::getHits
        ));

        events.forEach(e -> {
            e.setViews(
                    views.getOrDefault(views.get("/events/" + e.getId()), 0L)
            );
            e.setConfirmedRequests(confirmedRequests.getOrDefault(e.getId(), 0L));
        });

    }

    private Map<Long, Long> countConfirmedRequests(List<Long> eventsId) {

        List<ConfirmedRequestsDto> confirmedRequestsDtos = requestRepository.countConfirmedRequests(eventsId);

        return confirmedRequestsDtos.stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEventId, ConfirmedRequestsDto::getViews));
    }

}
