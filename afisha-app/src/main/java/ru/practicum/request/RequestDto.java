package ru.practicum.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestDto {

    private long id;

    @ManyToOne
    private User requestor;

    @ManyToOne
    private Event event;

    String status;

    LocalDateTime created;

}
