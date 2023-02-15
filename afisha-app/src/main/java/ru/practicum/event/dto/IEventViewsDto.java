package ru.practicum.event.dto;

import java.time.LocalDateTime;

public interface IEventViewsDto {

    long getId();

    LocalDateTime getEventDate();

    void setViews(Long views);

    void setConfirmedRequests(Long confirmedRequests);

}
