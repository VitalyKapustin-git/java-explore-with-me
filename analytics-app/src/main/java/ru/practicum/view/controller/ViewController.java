package ru.practicum.view.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.view.dto.StatsDto;
import ru.practicum.view.dto.ViewDto;
import ru.practicum.view.service.ViewService;

import java.util.List;

@RestController
@AllArgsConstructor
public class ViewController {

    private ViewService viewService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewDto saveRequest(@RequestBody ViewDto viewDto) {
        return viewService.saveRequest(viewDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") Boolean unique) {
        return viewService.getStats(start, end, uris, unique);
    }

}
