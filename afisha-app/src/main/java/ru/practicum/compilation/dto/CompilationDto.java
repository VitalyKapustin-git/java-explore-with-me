package ru.practicum.compilation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompilationDto {

    private long id;

    @NotNull
    private Boolean pinned;

    @NotBlank
    private String title;

    @JsonProperty(value = "events")
    private List<EventShortDto> events;

}
