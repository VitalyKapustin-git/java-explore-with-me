package ru.practicum.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    private List<RequestShortDto> confirmedRequests;

    private List<RequestShortDto> rejectedRequests;

}
