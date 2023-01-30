package ru.practicum.core.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class ServletRequestHandler {

    private static final String STATS_POST_URL = "http://localhost:9090/hit";

    private static final String STATS_GET_URL = "http://localhost:9090/stats";

    public void handleHttpReq(HttpServletRequest httpServletRequest) {

        HttpRequestObject httpRequestObject = new HttpRequestObject();
        RestTemplate restTemplate = new RestTemplate();

        httpRequestObject.setIp(httpServletRequest.getRemoteAddr());
        httpRequestObject.setUri(httpServletRequest.getRequestURI());

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

        JsonNode root = mapper.readTree(response.getBody());

        if(root.isNull() || root.size() == 0) return 0L;

        JsonNode views = root.get(0).path("hits");

        if(views == null) return 0L;
        if(views.asLong() == 1) return 1L;

        return views.asLong() - 1;

    }

}
