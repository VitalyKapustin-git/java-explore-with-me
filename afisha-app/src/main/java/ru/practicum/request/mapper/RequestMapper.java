package ru.practicum.request.mapper;

import ru.practicum.request.dto.RequestShortDto;
import ru.practicum.request.model.Request;

public class RequestMapper {

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
