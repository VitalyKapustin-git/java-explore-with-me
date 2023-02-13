package ru.practicum.request.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.exceptions.ConflictException;
import ru.practicum.core.exceptions.NotOwnerException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.ConfirmedRequestsDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestShortDto;
import ru.practicum.request.dto.RequestStatusDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.dao.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventService eventService;


    // Циклическая зависимость eventServiceImpl <-> requestServiceImpl. Здесь нужен
    // сервис событий, так как в его методах логика для наполнения расширенной DTO
    // данными по принятым запросам на участие
    // Используется ленивая подгрузка @Lazy. Без событий не будет и запросов, так что
    // вполне логично подгружать сервис событий в момент когда начинаем работать с
    // запросами на участие
    @Autowired
    RequestServiceImpl(RequestRepository requestRepository,
                       UserRepository userRepository,
                       @Lazy EventService eventService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestShortDto> userRequests(long userId) {
        return requestRepository.getOwnRequestsForNotOwnEvents(userId).stream()
                .map(RequestMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestShortDto createUserRequest(long userId, long eventId) throws JsonProcessingException {

        // Проверка на повторный запрос
        Boolean reqExists = requestRepository.checkIfUserRequestAlreadyExists(userId, eventId);
        if (reqExists != null)
            throw new ConflictException("Request from user with id=" + userId + " already exists");

        EventFullDto eventFullDto = eventService.getEventById(eventId);

        // Инициатор события не может добавить запрос на участие в своём событии
        if (eventFullDto.getInitiator().getId() == userId)
            throw new ConflictException("You cant subscribe for own event");

        // Проверка что событие (event) уже опубликовано
        // нельзя участвовать в неопубликованном событии
        if (!eventFullDto.getState().equals(EventState.PUBLISHED.toString()))
            throw new ConflictException("You cant subscribe for non published event");

        // Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        if (eventFullDto.getConfirmedRequests() >= eventFullDto.getParticipantLimit())
            throw new ConflictException("No vacant slots for event");

        Request request = new Request();
        request.setRequester(userRepository.getUserById(userId));
        request.setEvent(EventMapper.toEvent(eventFullDto));
        request.setStatus(RequestStatus.PENDING.toString());

        requestRepository.save(request);

        // Если для события отключена пре-модерация запросов на участие,
        // то запрос должен автоматически перейти в состояние подтвержденного
        if (!request.getEvent().getRequestModeration()) {
            requestRepository.updateRequestStatus(request.getId(), "CONFIRMED");
            request.setStatus(RequestStatus.CONFIRMED.toString());
        }

        return RequestMapper.toShortDto(request);
    }

    @Override
    @Transactional
    public RequestShortDto revokeOwnRequest(long userId, long requestId) {

        validateOwner(userId, requestRepository.getRequesterId(requestId));
        requestRepository.revokeOwnRequest(userId, requestId, RequestStatus.CANCELED.toString());

        return RequestMapper.toShortDto(requestRepository.getRequestById(requestId));
    }

    // Private: События
    // Получение информации о запросах на участие в событии текущего пользователя
    @Override
    @Transactional(readOnly = true)
    public List<RequestShortDto> getEventRequestsForUser(long userId, long eventId) {
        return requestRepository.getRequestsByEvent_IdAndEvent_Initiator_Id(eventId, userId).stream()
                .map(RequestMapper::toShortDto)
                .collect(Collectors.toList());
    }

    // Новый статус для заявок на участие в событии текущего пользователя
    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatusForOwnEvent(long userId,
                                                                         long eventId,
                                                                         RequestStatusDto requestStatusDto)
            throws JsonProcessingException {

        EventFullDto event = eventService.getEventById(eventId);

        if (event.getConfirmedRequests() == event.getParticipantLimit())
            throw new ConflictException("No available tickets");

        validateOwner(userId, event.getInitiator().getId());
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<Long> reqToBeChanged = requestStatusDto.getRequestIds();
        String newStatus = requestStatusDto.getStatus().toString();

        // Апдейтим статус во всех запросах на участие, id которых указаны в теле запроса
        requestRepository.updateRequestsStatus(reqToBeChanged, newStatus);

        // Получаем все запросы на участие и проставляем тем, чьи idшники указаны в теле запроса
        // новый статус, так как из-за апдейта не получится получить запросы на участие с уже обновленным статусом
        List<RequestShortDto> eventRequests = requestRepository
                .getRequestsByEvent_IdAndEvent_Initiator_Id(eventId, userId)
                .stream()
                .peek(request -> {
                    if (reqToBeChanged.contains(request.getId())) request.setStatus(newStatus);
                })
                .map(RequestMapper::toShortDto)
                .collect(Collectors.toList());

        // Раскладываем запросы на участие на подтвержденные и отклоненные
        result.setRejectedRequests(
                eventRequests.stream()
                        .filter(r -> requestStatusDto.getStatus() == RequestStatus.REJECTED)
                        .collect(Collectors.toList())
        );

        result.setConfirmedRequests(
                eventRequests.stream()
                        .filter(r -> requestStatusDto.getStatus() == RequestStatus.CONFIRMED)
                        .collect(Collectors.toList())
        );

        return result;
    }

    @Transactional
    public void validateOwner(long userId, long requesterId) {
        if (userId != requesterId) throw new NotOwnerException("User with id=" + userId + " is not owner of entity");
    }

    public Map<Long, Long> countConfirmedRequests(List<Long> eventsId) {

        List<ConfirmedRequestsDto> confirmedRequestsDtos = requestRepository.countConfirmedRequests(eventsId);

        return confirmedRequestsDtos.stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEventId, ConfirmedRequestsDto::getViews));
    }

}
