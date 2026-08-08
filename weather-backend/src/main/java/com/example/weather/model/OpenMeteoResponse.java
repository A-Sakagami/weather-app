package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Open-Meteo APIから返される天気情報を表すレスポンス。
 *
 * <p>
 * APIレスポンスに含まれる地点の緯度・経度と、
 * 現在の天気情報を保持する。
 *
 * <p>
 * {@code OpenMeteoClient}がAPIから受け取ったJSONを
 * このレコードへ変換し、WeatherServiceが{@link CurrentWeather}から
 * 気温、天気コード、風速、観測時刻を取り出す。
 *
 * <p>
 * JSONの{@code current}オブジェクトは、
 * 内部レコードの{@link CurrentWeather}へ対応付けられる。
 *
 * @param latitude  天気情報を取得した地点の緯度
 * @param longitude 天気情報を取得した地点の経度
 * @param current   現在の天気情報
 */
public record OpenMeteoResponse(
                double latitude,
                double longitude,
                CurrentWeather current) {

        /**
         * Open-Meteo APIから返される現在の天気情報を表す。
         *
         * <p>
         * 観測時刻、地上2メートル地点の気温、
         * 天気の状態を表す数値コード、
         * 地上10メートル地点の風速を保持する。
         *
         * <p>
         * Open-Meteo APIのJSONでは、
         * {@code temperature_2m}のようなスネークケースの名前が使われる。
         * Java側では{@link JsonProperty}を指定し、
         * {@code temperature}などのキャメルケースの要素へ対応付けている。
         *
         * @param time        対象地点の現地時刻を表す文字列
         * @param temperature 地上2メートル地点の現在の気温（摂氏）
         * @param weatherCode 天気の状態を表すOpen-Meteoの数値コード
         * @param windSpeed   地上10メートル地点の現在の風速（メートル毎秒）
         */
        public record CurrentWeather(
                        /*
                        * JSONのプロパティ名も「time」であるため、
                        * JsonPropertyを指定しなくても自動的に対応付けられる。
                        *
                        * この文字列はWeatherServiceでLocalDateTimeへ変換され、
                        * 都市のタイムゾーンを持つ日時として処理される。
                        */
                        String time,
                        /*
                         * JSONの「temperature_2m」を、Java側のtemperatureへ対応付ける。
                         */
                        @JsonProperty("temperature_2m") double temperature,
                        /*
                         * JSONの「weather_code」を、Java側のweatherCodeへ対応付ける。
                         */
                        @JsonProperty("weather_code") int weatherCode,
                        /*
                         * JSONの「wind_speed_10m」を、Java側のwindSpeedへ対応付ける。
                         */
                        @JsonProperty("wind_speed_10m") double windSpeed) {
        }
}