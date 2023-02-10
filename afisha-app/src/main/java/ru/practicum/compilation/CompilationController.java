package ru.practicum.compilation;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class CompilationController {

    private final CompilationService compilationService;

    // Public: Подборки событий
    // Получение подборок событий
    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) boolean pinned,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return compilationService.getAllCompilations(pinned, from, size);
    }

    // Получение подборки событий по его id
    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        return compilationService.getCompilationById(compId);
    }


    // Admin: Подборки событий
    // Добавление новой подборки (подборка может не содержать событий)
    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody CompilationNewDto compilationNewDto) throws JsonProcessingException {
        return compilationService.addCompilation(compilationNewDto);
    }

    // Удаление подборки
    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompilationById(@PathVariable long compId) {

        compilationService.removeCompilationById(compId);
    }

    // Обновить информацию о подборке
    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilations(@PathVariable long compId,
                                             @RequestBody CompilationUpdateRequestDto compilationUpdateRequestDto)
            throws JsonProcessingException {
        return compilationService.updateCompilations(compId, compilationUpdateRequestDto);
    }

}
