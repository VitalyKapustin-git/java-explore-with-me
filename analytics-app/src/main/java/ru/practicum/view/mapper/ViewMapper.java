package ru.practicum.view.mapper;

import ru.practicum.view.dto.ViewDto;
import ru.practicum.view.model.View;

public class ViewMapper {

    public static View toView(ViewDto viewDto) {

        View view = new View();

        view.setId(viewDto.getId());
        view.setIp(viewDto.getIp());
        view.setApp(viewDto.getApp());
        view.setUri(viewDto.getUri());
        view.setDate(viewDto.getDate());

        return view;
    }

}
