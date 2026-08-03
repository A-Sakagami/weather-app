package com.example.weather.client;

import com.example.weather.config.WeatherApiProperties;
import com.example.weather.model.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Open-Meteo APIクライアント。指定された緯度、経度、およびタイムゾーンに基づいて、現在の天気情報を取得する。
 * OpenMeteoClient
 */
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
            String timezone
    ) {
        // Open-Meteo APIを呼び出して、指定された緯度、経度、およびタイムゾーンに基づいて現在の天気情報を取得する
        // 返却されるレスポンスはOpenMeteoResponseクラスにマッピングされる
        // APIのエンドポイントは、緯度、経度、取得する情報（temperature_2m, weather_code, wind_speed_10m）、およびタイムゾーンをクエリパラメータとして指定する
        // エンドポイントの例: /v1/forecast?latitude=35.6895&longitude=139.6917&current=temperature_2m,weather_code,wind_speed_10m&timezone=Asia/Tokyo
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam(
                                "current",
                                "temperature_2m,weather_code,wind_speed_10m"
                        )
                        .queryParam("timezone", timezone)
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);
    }
}