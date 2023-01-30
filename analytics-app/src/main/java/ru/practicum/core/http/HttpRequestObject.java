package ru.practicum.core.http;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Объект для обращения из основного сервиса до аналитики (сохранение
// обращения в БД)
@NoArgsConstructor
@Getter
@Setter
public class HttpRequestObject {

    private String app = "ewm-main-service-spec";

    private String uri;

    private String ip;

    private LocalDateTime date = LocalDateTime.now();

}
