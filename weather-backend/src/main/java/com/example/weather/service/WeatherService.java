package com.example.weather.service;

import com.example.weather.client.OpenMeteoClient;
// import com.example.weather.model.City;
import com.example.weather.model.GeocodingResponse;
import com.example.weather.model.OpenMeteoResponse;
import com.example.weather.model.WeatherCondition;
import com.example.weather.model.WeatherResponse;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

/**
 * 気象関連の処理を行うためのサービスクラス。
 * @param openMeteoClient OpenMeteo APIクライアント
 * @param geocodingService ジオコーディングサービス
 */
@Service
public class WeatherService {

    private final OpenMeteoClient openMeteoClient;
    private final GeocodingService geocodingService;

    public WeatherService(OpenMeteoClient openMeteoClient, GeocodingService geocodingService) {
        this.openMeteoClient = openMeteoClient;
        this.geocodingService = geocodingService;
    }

    public WeatherResponse getWeather(String cityName, String country) {
        // OpenMeteoClientを使用して、指定された都市の現在の天気情報を取得する
        GeocodingResponse.Result location =
                geocodingService.searchCity(cityName, country);

        OpenMeteoResponse apiResponse =
                openMeteoClient.getCurrentWeather(
                        location.latitude(),
                        location.longitude(), 
                        location.timezone()
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
                        
        // 現在の天気情報の時間をLocalDateTimeに変換し、指定されたタイムゾーンでOffsetDateTimeに変換する
        LocalDateTime localDateTime =
                LocalDateTime.parse(current.time());
        OffsetDateTime observedAt =
                localDateTime
                        .atZone(ZoneId.of(location.timezone()))
                        .toOffsetDateTime();

        // JSON形式のWeatherResponseを作成して返す
        return new WeatherResponse(
                "success",
                location.name(),
                location.admin1(),
                location.country(),
                current.temperature(),
                current.weatherCode(),
                condition.getDescription(),
                current.windSpeed(),
                observedAt
        );
    }
}