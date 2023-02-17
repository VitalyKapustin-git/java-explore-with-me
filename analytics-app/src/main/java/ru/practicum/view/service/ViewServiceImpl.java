package ru.practicum.view.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.view.dao.ViewRepository;
import ru.practicum.view.dto.StatsDto;
import ru.practicum.view.dto.ViewDto;
import ru.practicum.view.mapper.ViewMapper;
import ru.practicum.view.model.View;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class ViewServiceImpl implements ViewService {

    private ViewRepository viewRepository;

    @Transactional
    @Override
    public ViewDto saveRequest(ViewDto viewDto) {

        viewDto.setDate(LocalDateTime.now());

        View view = viewRepository.save(ViewMapper.toView(viewDto));
        viewDto.setId(view.getId());

        return viewDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {

        LocalDateTime startDateTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime startEndTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (uris == null) {

            if (!unique) {
                return viewRepository.getAllViewsNonUnique(startDateTime, startEndTime);
            } else {
                return viewRepository.getAllViewsUnique(startDateTime, startEndTime);
            }

        } else {

            if (!unique) {
                return viewRepository.getViewsNonUniqueByUris(startDateTime, startEndTime, uris);
            } else {
                return viewRepository.getViewsUniqueByUris(startDateTime, startEndTime, uris);
            }

        }

    }

}
