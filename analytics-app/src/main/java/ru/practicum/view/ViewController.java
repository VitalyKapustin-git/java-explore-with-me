package ru.practicum.view;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class ViewController {

    private ViewService viewService;

    @PostMapping("/hit")
    public ViewDto saveRequest(@RequestBody ViewDto viewDto) {
        return viewService.saveRequest(viewDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") Boolean unique) {
        System.out.println("CONTROLLER");

        return viewService.getStats(start, end, uris, unique);
    }

}
