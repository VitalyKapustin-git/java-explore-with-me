package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EventUpdateAdminDto {

    private Long id;

    private String annotation;

    private CategoryDto categoryDto;

    private Integer category;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private String title;

    private String stateAction;

}
