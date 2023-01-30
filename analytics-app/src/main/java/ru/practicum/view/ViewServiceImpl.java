package ru.practicum.view;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        System.out.println("SERVICE START");

        viewDto.setDate(LocalDateTime.now());

        View view = viewRepository.save(ViewMapper.toView(viewDto));
        viewDto.setId(view.getId());

        System.out.println("SERVICE END");

        return viewDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {

        System.out.println(start);
        System.out.println(end);
        System.out.println("GET STATS START");
        System.out.println(uris);

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
