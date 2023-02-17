package ru.practicum.core.http;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.view.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatsHttpClient {

    private static final String STATS_POST_URL = "http://stats-server:9090/hit";

    private static final String STATS_GET_URL = "http://stats-server:9090/stats";

    public void saveView(String remoteAddr, String requestURI) {

        HttpRequestObject httpRequestObject = new HttpRequestObject();
        RestTemplate restTemplate = new RestTemplate();

        httpRequestObject.setIp(remoteAddr);
        httpRequestObject.setUri(requestURI);

        HttpEntity<HttpRequestObject> request = new HttpEntity<>(httpRequestObject);
        restTemplate.postForObject(STATS_POST_URL, request, HttpRequestObject.class);

    }

    public List<StatsDto> getViews(String uri,
                                   LocalDateTime eventStartDate) {

        RestTemplate restTemplate = new RestTemplate();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(STATS_GET_URL)
                .queryParam("start", eventStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("uris", uri)
                .build(false)
                .toUriString();

        ResponseEntity<List<StatsDto>> response =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<StatsDto>>() {
                        }
                );

        // В ответ на запрос возвращается либо массив с кол-ом просмотров, либо пустой массив (актуально для только что
        // созданных событий, которые еще никто не смотрел и о которых нет записей о просмотрах).
        if (response.getBody() == null || response.getBody().size() == 0) return new ArrayList<>();

        return response.getBody();

    }

}
