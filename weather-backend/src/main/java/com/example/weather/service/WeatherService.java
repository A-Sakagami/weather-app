package com.example.weather.service;

import com.example.weather.client.OpenMeteoClient;
import com.example.weather.model.GeocodingResponse;
import com.example.weather.model.OpenMeteoResponse;
import com.example.weather.model.WeatherCondition;
import com.example.weather.model.WeatherResponse;

import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final OpenMeteoClient openMeteoClient;
    private final GeocodingService geocodingService;

    public WeatherService(
            OpenMeteoClient openMeteoClient,
            GeocodingService geocodingService
    ) {
        this.openMeteoClient = openMeteoClient;
        this.geocodingService = geocodingService;
    }

    public WeatherResponse getWeather(String cityName) {
        return getWeather(cityName, null);
    }

    public WeatherResponse getWeather(
            String cityName,
            String country
    ) {
        GeocodingResponse.Result location =
                geocodingService.searchCity(cityName, country);

        boolean useJmaMsm =
                "日本".equals(location.country());

        OpenMeteoResponse apiResponse =
                openMeteoClient.getCurrentWeather(
                        location.latitude(),
                        location.longitude(),
                        location.timezone(),
                        useJmaMsm
                );

        if (apiResponse == null || apiResponse.current() == null) {
            throw new IllegalStateException(
                    "天気情報の形式が正しくありません"
            );
        }

        OpenMeteoResponse.CurrentWeather current =
                apiResponse.current();

        WeatherCondition condition =
                WeatherCondition.fromCurrentWeather(
                        current.weatherCode(),
                        current.precipitation(),
                        current.rain(),
                        current.showers()
                );

        /*
        * 地名検索で得た都市のタイムゾーンを設定し、
        * UTCオフセットを持つOffsetDateTimeへ変換する。
        *
        * これにより、東京やロンドンなど、都市ごとの時差を
        * 含んだ時刻としてレスポンスへ格納できる。
        */
        OffsetDateTime observedAt =
                LocalDateTime.parse(current.time())
                        .atZone(ZoneId.of(location.timezone()))
                        .toOffsetDateTime();

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