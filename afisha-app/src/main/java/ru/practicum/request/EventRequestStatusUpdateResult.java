package ru.practicum.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    List<RequestShortDto> confirmedRequests;

    List<RequestShortDto> rejectedRequests;

}
