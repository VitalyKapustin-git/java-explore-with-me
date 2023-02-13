package ru.practicum.compilation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateRequestDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinnedFilter, int from, int size);

    CompilationDto getCompilationById(long compId);

    CompilationDto addCompilation(CompilationNewDto compilationNewDto) throws JsonProcessingException;

    void removeCompilationById(long compId);

    CompilationDto updateCompilations(long compId, CompilationUpdateRequestDto compilationUpdateRequestDto) throws JsonProcessingException;
}
