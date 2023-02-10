package ru.practicum.core.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.view.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsHttpClient {

    private static final String STATS_POST_URL = "http://localhost:9090/hit";

    private static final String STATS_GET_URL = "http://localhost:9090/stats";

    public void saveView(String remoteAddr, String requestURI) {

        HttpRequestObject httpRequestObject = new HttpRequestObject();
        RestTemplate restTemplate = new RestTemplate();

        httpRequestObject.setIp(remoteAddr);
        httpRequestObject.setUri(requestURI);

        HttpEntity<HttpRequestObject> request = new HttpEntity<>(httpRequestObject);
        restTemplate.postForObject(STATS_POST_URL, request, HttpRequestObject.class);

    }

    public Long getViews(String uri,
                         LocalDateTime eventStartDate)
            throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(STATS_GET_URL)
                .queryParam("start", eventStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("uris", uri)
                .build(false)
                .toUriString();

        ResponseEntity<String> response
                = restTemplate.getForEntity(urlTemplate, String.class);

        // В ответ на запрос возвращается либо массив с кол-ом просмотров, либо пустой массив (актуально для только что
        // созданных событий, которые еще никто не смотрел и о которых нет записей о просмотрах).
        if (response.getBody() == null || response.getBody().length() == 2) return 0L;

        StatsDto root = mapper.readValue(response.getBody()
                .replace("[", "")
                .replace("]", ""), StatsDto.class);

        long views = root.getHits();

        return views == 1 ? 1L : views;

    }

}
