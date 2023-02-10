package ru.practicum.compilation;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinnedFilter, int from, int size);

    CompilationDto getCompilationById(long compId);

    CompilationDto addCompilation(CompilationNewDto compilationNewDto) throws JsonProcessingException;

    void removeCompilationById(long compId);

    CompilationDto updateCompilations(long compId, CompilationUpdateRequestDto compilationUpdateRequestDto) throws JsonProcessingException;
}
