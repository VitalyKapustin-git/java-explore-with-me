package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {

        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());
        if (compilation.getEvents() == null) {
            compilationDto.setEvents(new ArrayList<>());
        } else {
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList()));
        }

        return compilationDto;

    }

    public static Compilation toCompilation(CompilationNewDto compilationNewDto) {

        Compilation compilation = new Compilation();

        compilation.setId(compilationNewDto.getId());
        compilation.setPinned(compilationNewDto.getPinned());
        compilation.setTitle(compilationNewDto.getTitle());

        return compilation;

    }

}
