package ru.practicum.compilation;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Primary
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventService eventService;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinnedFilter, int from, int size) {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        if (pinnedFilter == null) {
            return compilationRepository.findAll(pageable).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }

        if (pinnedFilter) {
            return compilationRepository.getCompilationsByPinnedIsTrue(pageable).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }

        return compilationRepository.getCompilationsByPinnedIsFalse(pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());


    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long compId) {
        return CompilationMapper.toCompilationDto(compilationRepository.getCompilationById(compId));
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(CompilationNewDto compilationNewDto) throws JsonProcessingException {

        Compilation compilation = CompilationMapper.toCompilation(compilationNewDto);
        if(compilation.getEvents() != null) {
            compilation.setEvents(eventService.getEventsById(compilationNewDto.getEvents()).stream()
                    .map(EventMapper::toEvent)
                    .collect(Collectors.toList()));
        }

        compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void removeCompilationById(long compId) {
        compilationRepository.removeCompilationById(compId);
    }

    @Override
    public CompilationDto updateCompilations(long compId, CompilationUpdateRequestDto compilationUpdateRequestDto) throws JsonProcessingException {

        Compilation compilation = compilationRepository.getCompilationById(compId);

        Boolean pinned = compilationUpdateRequestDto.getPinned();
        if (pinned != null) compilation.setPinned(pinned);

        String title = compilationUpdateRequestDto.getTitle();
        if (title != null && !title.isBlank()) compilation.setTitle(title);

        // Новый список id событий которые должны быть в подборке
        List<Long> newEventsIds = compilationUpdateRequestDto.getEvents();
        if (newEventsIds != null && newEventsIds.size() != 0) {

            List<Event> events = eventService.getEventsById(newEventsIds).stream()
                    .map(EventMapper::toEvent)
                    .collect(Collectors.toList());

            compilation.setEvents(events);
            compilationRepository.save(compilation);

        }

        return CompilationMapper.toCompilationDto(compilation);
    }

}
