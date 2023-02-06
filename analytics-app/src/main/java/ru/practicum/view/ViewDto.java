package ru.practicum.view;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ViewDto {

    private long id;

    @NotBlank
    private String ip;

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotNull
    private LocalDateTime date;

}
