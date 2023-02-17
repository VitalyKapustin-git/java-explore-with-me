package ru.practicum.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CompilationNewDto {

    private long id;

    @NotNull(message = "You must specify pinned field")
    private Boolean pinned;

    @NotBlank(message = "You must specify title field content")
    private String title;

    private List<Long> events;

}
