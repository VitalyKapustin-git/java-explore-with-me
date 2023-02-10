package ru.practicum.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryService;
import ru.practicum.core.exceptions.ConflictException;
import ru.practicum.core.exceptions.NotFoundException;
import ru.practicum.core.http.StatsHttpClient;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserService userService;

    private final CategoryService categoryService;

    private final EntityManager entityManager;

    private final HttpServletRequest request;

    private final StatsHttpClient statsHttpClient;


    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long eventId) throws JsonProcessingException {

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.getEventById(eventId));
        Long eventViews = statsHttpClient.getViews(request.getRequestURI(), eventFullDto.getCreatedOn());
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
                                            int size) throws JsonProcessingException {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        List<Event> findByCriterias = eventRepository.findAll((Specification<Event>) (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(text != null && !text.isBlank()) {
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("description")),
                                        "%" + text.toLowerCase() + "%"),
                                cb.like(cb.lower(root.get("annotation")),
                                        "%" + text.toLowerCase() + "%")
                        )
                );
            }

            if(categories != null && categories.size() > 0) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if(paid != null) {

                if (paid) {
                    predicates.add(cb.isTrue(root.get("paid")));
                }

                if (!paid) {
                    predicates.add(cb.isFalse(root.get("paid")));
                }
            }

            if(rangeStart != null && rangeEnd != null) {

                if (rangeStart.isAfter(rangeEnd)) throw new NotFoundException("Start date could not be after end date");
                predicates.add(cb.between(root.get("eventDate"), rangeStart, rangeEnd));
            }

            if(rangeStart != null && rangeEnd == null) {
                predicates.add(cb.greaterThan(root.get("eventDate"), rangeStart));
            }

            if(rangeStart == null && rangeEnd != null) {
                predicates.add(cb.lessThan(root.get("eventDate"), rangeEnd));
            }

            if(sort != null && sort.equalsIgnoreCase("EVENT_DATE")) {
                cq.orderBy(cb.desc(root.get("eventDate")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).getContent();

        List<EventShortDto> eventShortDtos = findByCriterias.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        // Сравнение кол-ва запросов на участие с кол-ом участником в самом эвенте
        Map<Long, Long> confirmedRequests = countConfirmedRequests(eventShortDtos.stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList()));

        for(EventShortDto eventShortDto : eventShortDtos) {
            eventShortDto.setViews(statsHttpClient.getViews("/events/" + eventShortDto.getId(),
                    eventShortDto.getCreatedOn()));
            eventShortDto.setConfirmedRequests(
                    confirmedRequests.get(eventShortDto.getId()) == null ?
                            0L : confirmedRequests.get(eventShortDto.getId())
                    );
        }

        // Проверка, есть ли в событии еще доступные места или нет (сравнение подтвержденных заявок и кол-ва мест на соб)
        if(onlyAvailable) eventShortDtos = eventShortDtos.stream()
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
    public List<EventShortDto> getEventsByUserId(long userId, int from, int size) throws JsonProcessingException {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        List<EventShortDto> eventShortDtos = eventRepository.getEventsByInitiatorId(userId, pageable).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = countConfirmedRequests(eventShortDtos.stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList())
        );

        for(EventShortDto eventShortDto : eventShortDtos) {
            eventShortDto.setViews(statsHttpClient.getViews("/events/" + eventShortDto.getId(),
                    eventShortDto.getCreatedOn()));
            eventShortDto.setConfirmedRequests(
                    confirmedRequests.get(eventShortDto.getId()) == null ?
                            0L : confirmedRequests.get(eventShortDto.getId())
            );
        }

        return eventShortDtos;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, EventNewDto eventNewDto) {

        if(eventNewDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
            throw new ConflictException("Event date must be in future and +2 hours from actual time");

        eventNewDto.setCategoryDto(categoryService.getCategory(eventNewDto.getCategory()));

        Event event = EventMapper.toEvent(eventNewDto);
        event.setInitiator(UserMapper.toUser(userService.getUsers(List.of(userId), 0, 1).get(0)));
        event.setState(EventState.PENDING.toString());
        eventRepository.save(event);

        return EventMapper.toEventFullDto(event);

    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getOwnFullEventByUserId(long userId, long eventId) throws JsonProcessingException {

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.getEventByIdAndInitiator_Id(eventId, userId));
        Long confirmedRequests = countConfirmedRequests(List.of(eventFullDto.getId()))
                .get(eventFullDto.getId());

        eventFullDto.setViews(statsHttpClient.getViews("/events/" + eventFullDto.getId(),
                eventFullDto.getCreatedOn()));
        eventFullDto.setConfirmedRequests(confirmedRequests == null ? 0L : confirmedRequests);

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto changeEventByOwnerUser(long userId, long eventId,
                                        EventUpdateAdminDto eventUpdateAdminDto) {

        LocalDateTime newEventTime = eventUpdateAdminDto.getEventDate();
        if(newEventTime != null) {
            if (eventUpdateAdminDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
                throw new ConflictException("Event date must be in future and +2 hours from actual time");
        }

        Event oldEvent = eventRepository.getEventById(eventId);
        if(oldEvent == null) throw new NotFoundException("Not found event with id=" + eventId);
        if(oldEvent.getInitiator().getId() != userId) throw new ConflictException("You are not event owner");
        if(oldEvent.getState().equals("PUBLISHED")) throw new ConflictException("Can't edit PUBLISHED event");

        extractEvent(oldEvent, eventUpdateAdminDto);

        return EventMapper.toEventFullDto(eventRepository.save(oldEvent));
    }


    // Admin: События
    // Поиск событий
    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEventsFull(List<Long> usersId,
                                               List<String> states,
                                               List<Integer> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               int from,
                                               int size) throws JsonProcessingException {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        // Некорректная обработка в тестах postman. Ранее был только EventState = PUBLISHED
        // с февраля похоже заменилось на PUBLISH_EVENT

        List<Event> findByCriterias = eventRepository.findAll((Specification<Event>) (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(usersId != null && usersId.size() > 0) {
                predicates.add(root.get("initiator").get("id").in(usersId));
            }

            if(states != null && states.size() > 0) {
                predicates.add(root.get("state").in(states));
            }

            if(categories != null && categories.size() > 0) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if(rangeStart != null && rangeEnd != null) {

                if (rangeStart.isAfter(rangeEnd)) throw new NotFoundException("Start date could not be after end date");
                predicates.add(cb.between(root.get("eventDate"), rangeStart, rangeEnd));
            }

            if(rangeStart != null && rangeEnd == null) {
                predicates.add(cb.greaterThan(root.get("eventDate"), rangeStart));
            }

            if(rangeStart == null && rangeEnd != null) {
                predicates.add(cb.lessThan(root.get("eventDate"), rangeEnd));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).getContent();

        List<EventFullDto> events = findByCriterias.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
        List<Long> eventsId = events.stream().map(EventFullDto::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = countConfirmedRequests(eventsId);

        for(EventFullDto e : events) {
            Long confReqNum = confirmedRequests.get(e.getId());
            if(confReqNum != null)
                e.setConfirmedRequests(confReqNum);

            e.setViews(statsHttpClient.getViews("/events/" + e.getId(),
                    e.getCreatedOn()));
        }

        return events;

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
    public List<EventFullDto> getEventsById(List<Long> ids) throws JsonProcessingException {

        List<EventFullDto> eventFullDtos = eventRepository.getEventsByIdIn(ids).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = countConfirmedRequests(ids);

        for(EventFullDto eventFullDto : eventFullDtos) {
            eventFullDto.setViews(statsHttpClient.getViews("/events/" + eventFullDto.getId(),
                    eventFullDto.getCreatedOn()));
            eventFullDto.setConfirmedRequests(
                    confirmedRequests.get(eventFullDto.getId()) == null ?
                            0L : confirmedRequests.get(eventFullDto.getId())
            );
        }

        return eventFullDtos;
    }

    private void extractEvent(Event oldEvent, EventUpdateAdminDto eventUpdateAdminDto) {

        if(eventUpdateAdminDto.getCategory() != null &&
                categoryService.getCategory(eventUpdateAdminDto.getCategory()) != null) {
            oldEvent.setCategory(CategoryMapper.toCategory(
                    categoryService.getCategory(eventUpdateAdminDto.getCategory()))
            );
        }

        if(eventUpdateAdminDto.getAnnotation() != null) oldEvent.setAnnotation(eventUpdateAdminDto.getAnnotation());
        if(!Optional.ofNullable(eventUpdateAdminDto.getDescription()).orElse("").isBlank())
            oldEvent.setDescription(eventUpdateAdminDto.getDescription());
        if(eventUpdateAdminDto.getEventDate() != null) oldEvent.setEventDate(eventUpdateAdminDto.getEventDate());
        if(eventUpdateAdminDto.getLocation() != null) {
            oldEvent.setLat(eventUpdateAdminDto.getLocation() == null ? 0.0F : eventUpdateAdminDto.getLocation().getLat());
            oldEvent.setLon(eventUpdateAdminDto.getLocation() == null ? 0.0F : eventUpdateAdminDto.getLocation().getLon());
        }
        if(eventUpdateAdminDto.getPaid() != null) oldEvent.setPaid(eventUpdateAdminDto.getPaid());
        if(eventUpdateAdminDto.getParticipantLimit() != null)
            oldEvent.setParticipantLimit(eventUpdateAdminDto.getParticipantLimit());
        if(eventUpdateAdminDto.getRequestModeration() != null)
            oldEvent.setRequestModeration(eventUpdateAdminDto.getRequestModeration());
        if(!Optional.ofNullable(eventUpdateAdminDto.getTitle()).orElse("").isBlank())
            oldEvent.setTitle(eventUpdateAdminDto.getTitle());
        if(eventUpdateAdminDto.getStateAction() != null) {
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

    // Подсчет подтвержденных запросов для событий по переданному набору id событий
    private Map<Long, Long> countConfirmedRequests(List<Long> reqIds) {
        return entityManager.createQuery("" +
                                "select r.event.id as event_id, count(r.id) as requests_num " +
                                "from Request r where r.status = 'CONFIRMED' and r.event.id in (" +
                                reqIds.toString().replace("[", "").replace("]", "")
                                + ") group by r.event.id"
                        , Tuple.class)
                .getResultStream()
                .collect(
                        Collectors.toMap(
                                tuple -> ((Number) tuple.get("event_id")).longValue(),
                                tuple -> ((Number) tuple.get("requests_num")).longValue()
                        )
                );
    }

    private void validateEvent(Event oldEvent, EventUpdateAdminDto eventUpdateAdminDto) {

        LocalDateTime newEventDate = eventUpdateAdminDto.getEventDate();
        String oldEventState = oldEvent.getState();
        String newEventState = eventUpdateAdminDto.getStateAction();

        if(newEventDate != null) {
            if (eventUpdateAdminDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
                throw new ConflictException("Event date must be in future and +2 hours from actual time");
        }

        if(newEventDate != null) {
            if (newEventDate.isBefore(LocalDateTime.now()))
                throw new ConflictException("You can't setup past date");
        }

        if(newEventState != null) {
            if(oldEventState.equals(EventState.PUBLISHED.toString())
                    && newEventState.equals("PUBLISH_EVENT"))
                throw new ConflictException("Event already published");
            if(oldEventState.equals(EventState.PUBLISHED.toString())
                    && newEventState.equals("REJECT_EVENT"))
                throw new ConflictException("Published event couldn't be cancelled");
            if(oldEventState.equals(EventState.CANCELED.toString())
                    && newEventState.equals("PUBLISH_EVENT")) {
                throw new ConflictException("Can't publish canceled event");
            }
        }
    }

}
