package ru.practicum.view;

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
