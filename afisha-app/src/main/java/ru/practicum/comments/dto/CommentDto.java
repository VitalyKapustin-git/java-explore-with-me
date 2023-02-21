package ru.practicum.comments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

    private long id;

    private UserDto author;

    private EventShortDto event;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String comment;

}
