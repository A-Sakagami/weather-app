package com.example.weather.service;

import com.example.weather.client.OpenMeteoClient;
import com.example.weather.model.OpenMeteoResponse;
import com.example.weather.model.WeatherCondition;
import com.example.weather.model.WeatherResponse;
import org.springframework.stereotype.Service;

/**
 * 気象関連の処理を行うためのサービスクラス。
 */
@Service
public class WeatherService {

    private static final double TOKYO_LATITUDE = 35.6895;
    private static final double TOKYO_LONGITUDE = 139.6917;

    private final OpenMeteoClient openMeteoClient;

    public WeatherService(OpenMeteoClient openMeteoClient) {
        this.openMeteoClient = openMeteoClient;
    }

    public WeatherResponse getWeather() {
        OpenMeteoResponse apiResponse =
                openMeteoClient.getCurrentWeather(
                        TOKYO_LATITUDE,
                        TOKYO_LONGITUDE
                );

        OpenMeteoResponse.CurrentWeather current =
                apiResponse.current();
        
        WeatherCondition condition =
                WeatherCondition.fromCode(current.weatherCode());


        return new WeatherResponse(
                "success",
                "東京",
                current.temperature(),
                current.weatherCode(),
                condition.getDescription(),
                current.windSpeed()
        );
    }
}