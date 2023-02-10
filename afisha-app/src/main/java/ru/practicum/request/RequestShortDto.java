package ru.practicum.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestShortDto {

    private long id;

    private long requester;

    private long event;

    private String status;

    private LocalDateTime created;

}
