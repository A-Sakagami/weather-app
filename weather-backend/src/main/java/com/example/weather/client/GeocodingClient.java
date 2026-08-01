package com.example.weather.client;

import com.example.weather.config.WeatherApiProperties;
import com.example.weather.model.GeocodingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeocodingClient {

    private final RestClient restClient;

    public GeocodingClient(WeatherApiProperties weatherApiProperties) {
        this.restClient = RestClient.builder()
                .baseUrl(weatherApiProperties.getGeocodingUrl())
                .build();
    }

    public GeocodingResponse search(String cityName) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("name", cityName)
                        .queryParam("count", 5)
                        .queryParam("language", "ja")
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .body(GeocodingResponse.class);
    }
}