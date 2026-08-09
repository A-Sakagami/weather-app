package com.example.weather.client;

import com.example.weather.config.WeatherApiProperties;
import com.example.weather.model.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenMeteoClient {

    private final RestClient restClient;

    public OpenMeteoClient(WeatherApiProperties weatherApiProperties) {
        this.restClient = RestClient.builder()
                .baseUrl(weatherApiProperties.getBaseUrl())
                .build();
    }

    public OpenMeteoResponse getCurrentWeather(
            double latitude,
            double longitude,
            String timezone,
            boolean useJmaMsm
    ) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam(
                                    "current",
                                    "temperature_2m,weather_code,wind_speed_10m,"
                                            + "precipitation,rain,showers"
                            )
                            .queryParam("timezone", timezone);

                    if (useJmaMsm) {
                        uriBuilder.queryParam(
                                "models",
                                "jma_msm"
                        );
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(OpenMeteoResponse.class);
    }
}