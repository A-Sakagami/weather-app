package com.example.weather.service;

import com.example.weather.client.OpenMeteoClient;
import com.example.weather.model.City;
import com.example.weather.model.OpenMeteoResponse;
import com.example.weather.model.WeatherCondition;
import com.example.weather.model.WeatherResponse;
import org.springframework.stereotype.Service;

/**
 * 気象関連の処理を行うためのサービスクラス。
 */
@Service
public class WeatherService {

    private final OpenMeteoClient openMeteoClient;

    public WeatherService(OpenMeteoClient openMeteoClient) {
        this.openMeteoClient = openMeteoClient;
    }

    public WeatherResponse getWeather() {
        return getWeather(City.TOKYO);
    }

    public WeatherResponse getWeather(City city) {
        // OpenMeteoClientを使用して、指定された都市の現在の天気情報を取得する
        OpenMeteoResponse apiResponse =
                openMeteoClient.getCurrentWeather(
                        city.getLatitude(),
                        city.getLongitude()
                );

        // 取得したAPIレスポンスがnullまたはcurrentがnullの場合、IllegalStateExceptionをスローする
        if (apiResponse == null || apiResponse.current() == null) {
            throw new IllegalStateException(
                    "天気情報の形式が正しくありません"
            );
        }
        // APIレスポンスから現在の天気情報を取得する
        OpenMeteoResponse.CurrentWeather current =
                apiResponse.current();

        // 天候コードからWeatherConditionを取得する
        WeatherCondition condition =
                WeatherCondition.fromCode(current.weatherCode());

        // WeatherResponseを作成して返す
        return new WeatherResponse(
                "success",
                city.getDisplayName(),
                current.temperature(),
                current.weatherCode(),
                condition.getDescription(),
                current.windSpeed()
        );
    }
}