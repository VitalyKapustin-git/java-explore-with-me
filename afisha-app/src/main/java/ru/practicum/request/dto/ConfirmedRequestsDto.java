package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConfirmedRequestsDto {

    private long eventId;

    private long views;

}
