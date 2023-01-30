package ru.practicum.view;

import java.util.List;

public interface ViewService {
    ViewDto saveRequest(ViewDto viewDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
