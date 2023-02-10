package ru.practicum.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.exceptions.ConflictException;
import ru.practicum.core.exceptions.NotOwnerException;
import ru.practicum.event.EventFullDto;
import ru.practicum.event.EventService;
import ru.practicum.event.EventState;
import ru.practicum.user.UserService;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Primary
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserService userService;

    private final EventService eventService;

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

        RequestDto requestDto = new RequestDto();

        requestDto.setRequester(userService.getUsers(List.of(userId), 0, 10).get(0));
        requestDto.setEvent(eventService.getEventById(eventId));

        Request request = requestRepository.save(RequestMapper.toRequest(requestDto));

        requestDto.setId(request.getId());

        // Если для события отключена пре-модерация запросов на участие,
        // то запрос должен автоматически перейти в состояние подтвержденного
        if (!requestDto.getEvent().getRequestModeration())
            request.setStatus(RequestStatus.CONFIRMED.toString());

        requestRepository.save(request);

        return RequestMapper.toShortDto(requestRepository.getRequestById(request.getId()));
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

    // Подтверждение чужой заявки на участие в событии текущего пользователя
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
        requestRepository.updateRequestStatus(reqToBeChanged, newStatus);

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

}
