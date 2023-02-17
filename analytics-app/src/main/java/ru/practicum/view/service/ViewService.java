package ru.practicum.view.service;

import ru.practicum.view.dto.StatsDto;
import ru.practicum.view.dto.ViewDto;

import java.util.List;

public interface ViewService {
    ViewDto saveRequest(ViewDto viewDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
