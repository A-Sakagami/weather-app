package com.example.weather.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * WeatherConditionの天候判定を確認する単体テスト。
 *
 * <p>現在の降水量を考慮するfromCurrentWeather()と、
 * 天気コードを変換するfromCode()の主な挙動を確認する。
 */
class WeatherConditionTest {

    /**
     * にわか雨量が記録されている場合、
     * 天気コードより優先して「にわか雨」と判定されることを確認する。
     */
    @Test
    @DisplayName("にわか雨量がある場合はRAIN_SHOWERを返す")
    void returnsRainShowerWhenShowersArePresent() {
        WeatherCondition actual =
                WeatherCondition.fromCurrentWeather(
                        3,      // 曇り
                        1.5,    // 総降水量
                        1.0,    // 雨量
                        0.5     // にわか雨量
                );

        assertEquals(WeatherCondition.RAIN_SHOWER, actual);
    }

    /**
     * 雨量が記録されている場合、
     * 天気コードより優先して「雨」と判定されることを確認する。
     */
    @Test
    @DisplayName("雨量がある場合はRAINを返す")
    void returnsRainWhenRainIsPresent() {
        WeatherCondition actual =
                WeatherCondition.fromCurrentWeather(
                        3,      // 曇り
                        1.0,    // 総降水量
                        1.0,    // 雨量
                        0.0     // にわか雨量
                );

        assertEquals(WeatherCondition.RAIN, actual);
    }

    /**
     * 個別の雨量が0でも総降水量が記録されていれば、
     * 現在の実装では「雨」と判定されることを確認する。
     */
    @Test
    @DisplayName("総降水量だけがある場合もRAINを返す")
    void returnsRainWhenOnlyPrecipitationIsPresent() {
        WeatherCondition actual =
                WeatherCondition.fromCurrentWeather(
                        2,      // 一部曇り
                        0.5,    // 総降水量
                        0.0,    // 雨量
                        0.0     // にわか雨量
                );

        assertEquals(WeatherCondition.RAIN, actual);
    }

    /**
     * 降水量がすべて0の場合、
     * 天気コードに対応する天候が返されることを確認する。
     */
    @Test
    @DisplayName("降水がない場合は天気コードで判定する")
    void usesWeatherCodeWhenThereIsNoPrecipitation() {
        WeatherCondition actual =
                WeatherCondition.fromCurrentWeather(
                        3,      // 曇り
                        0.0,
                        0.0,
                        0.0
                );

        assertEquals(WeatherCondition.CLOUDY, actual);
    }

    /**
     * 降水がなく、未対応の天気コードが渡された場合、
     * UNKNOWNが返されることを確認する。
     */
    @Test
    @DisplayName("未対応の天気コードではUNKNOWNを返す")
    void returnsUnknownForUnsupportedWeatherCode() {
        WeatherCondition actual =
                WeatherCondition.fromCurrentWeather(
                        -1,
                        0.0,
                        0.0,
                        0.0
                );

        assertEquals(WeatherCondition.UNKNOWN, actual);
    }

    /**
     * 雪の天気コードで総降水量が記録されている場合の
     * 現在の挙動を記録するテスト。
     *
     * <p>総降水量は雪も含むが、現在の実装では
     * precipitationが0より大きいとRAINが返される。
     */
    @Test
    @DisplayName("雪のコードでも総降水量がある場合は現在の実装ではRAINを返す")
    void returnsRainForSnowCodeWhenPrecipitationIsPresent() {
        WeatherCondition actual =
                WeatherCondition.fromCurrentWeather(
                        71,     // 弱い雪
                        1.0,    // 雪を含む総降水量
                        0.0,
                        0.0
                );

        assertEquals(WeatherCondition.RAIN, actual);
    }
}