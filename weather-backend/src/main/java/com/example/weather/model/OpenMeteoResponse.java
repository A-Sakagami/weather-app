package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OpenMeteoResponse
 * OpenMeteo APIのレスポンスを表すレコードクラス。
 * このクラスは、OpenMeteo APIから取得した天気情報を格納するためのデータ構造を提供する。
 * @param latitude 緯度
 * @param longitude 経度
 * @param current 現在の天気情報
 */
public record OpenMeteoResponse(
        double latitude,
        double longitude,
        CurrentWeather current
) {

    public record CurrentWeather(
            String time,

            @JsonProperty("temperature_2m")
            double temperature,

            @JsonProperty("weather_code")
            int weatherCode,

            @JsonProperty("wind_speed_10m")
            double windSpeed
    ) {
    }
}