package ru.practicum.request;

import ru.practicum.event.EventMapper;
import ru.practicum.user.UserMapper;

public class RequestMapper {

    public static Request toRequest(RequestDto requestDto) {

        Request request = new Request();

        request.setId(requestDto.getId());
        request.setRequester(UserMapper.toUser(requestDto.getRequester()));
        request.setStatus(requestDto.getStatus());
        request.setEvent(EventMapper.toEvent(requestDto.getEvent()));
        request.setCreated(requestDto.getCreated());

        return request;

    }

    public static RequestShortDto toShortDto(Request request) {

        RequestShortDto requestShortDto = new RequestShortDto();

        requestShortDto.setId(request.getId());
        requestShortDto.setRequester(request.getRequester().getId());
        requestShortDto.setStatus(request.getStatus());
        requestShortDto.setEvent(request.getEvent().getId());
        requestShortDto.setCreated(request.getCreated());

        return requestShortDto;

    }

}
