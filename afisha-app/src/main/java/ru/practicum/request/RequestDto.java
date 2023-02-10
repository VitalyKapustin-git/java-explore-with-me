package ru.practicum.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.EventFullDto;
import ru.practicum.user.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestDto {

    private long id;

    private UserDto requester;

    private EventFullDto event;

    private String status = "PENDING";

    private LocalDateTime created = LocalDateTime.now();

}
