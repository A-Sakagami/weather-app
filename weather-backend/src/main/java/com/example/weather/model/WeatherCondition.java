package com.example.weather.model;

/**
 * 天候の状態を表す列挙型。
 */
public enum WeatherCondition {

    CLEAR("晴れ"),
    MAINLY_CLEAR("おおむね晴れ"),
    PARTLY_CLOUDY("一部曇り"),
    CLOUDY("曇り"),
    FOG("霧"),
    DRIZZLE("霧雨"),
    RAIN("雨"),
    SNOW("雪"),
    RAIN_SHOWER("にわか雨"),
    SNOW_SHOWER("にわか雪"),
    THUNDERSTORM("雷雨"),
    UNKNOWN("不明");

    private final String description;

    WeatherCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static WeatherCondition fromCode(int weatherCode) {
        return switch (weatherCode) {
            case 0 -> CLEAR;
            case 1 -> MAINLY_CLEAR;
            case 2 -> PARTLY_CLOUDY;
            case 3 -> CLOUDY;
            case 45, 48 -> FOG;
            case 51, 53, 55, 56, 57 -> DRIZZLE;
            case 61, 63, 65, 66, 67 -> RAIN;
            case 71, 73, 75, 77 -> SNOW;
            case 80, 81, 82 -> RAIN_SHOWER;
            case 85, 86 -> SNOW_SHOWER;
            case 95, 96, 99 -> THUNDERSTORM;
            default -> UNKNOWN;
        };
    }
}