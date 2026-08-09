package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
            double windSpeed,

            double precipitation,

            double rain,

            double showers
    ) {
    }
}