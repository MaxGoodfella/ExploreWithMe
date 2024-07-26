package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class StatsClient {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;


    @Autowired
    public StatsClient(@Value("${stat_server.url}") String serviceUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }


    public void hit(StatsDto request) {
        restTemplate.postForObject("/hit", request, Void.class);
    }

    public List<StatsViewDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String formattedStart = start.format(formatter);
        String formattedEnd = end.format(formatter);

        StringBuilder urlBuilder = new StringBuilder("/stats?");
        urlBuilder.append("start=").append(formattedStart);
        urlBuilder.append("&end=").append(formattedEnd);
        urlBuilder.append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                urlBuilder.append("&uris=").append(uri);
            }
        }

        String url = urlBuilder.toString();

        ResponseEntity<List<StatsViewDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

}