package ru.practicum.compilation;

import ru.practicum.event.EventMapper;

import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {

        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList()));

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
