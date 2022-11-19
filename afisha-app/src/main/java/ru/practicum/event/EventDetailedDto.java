package ru.practicum.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.CategoryDto;
import ru.practicum.user.UserDto;

import javax.persistence.Enumerated;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EventDetailedDto {

    private long id;

    @Size(min = 20, message = "{validation.name.size.too_short}")
    @Size(max = 2000, message = "{validation.name.size.too_long}")
    private String annotation;

    private CategoryDto categoryDto;

    private LocalDateTime createdOn;

    private UserDto initiatorDto;

    private LocalDateTime eventDate;

    @JsonProperty("paid")
    boolean paid;

    private int participantLimit;

    private LocalDateTime publishedOn;

    private boolean requestModeration;

    @Enumerated
    private EventState state;

    private String title;

    private float lat;

    private float lon;
}
