package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Open-Meteo APIから返される現在の天気情報を表すレスポンス。
 *
 * <p>APIレスポンスに含まれる地点の緯度・経度と、
 * 現在の天気情報を保持する。
 *
 * <p>{@code OpenMeteoClient}がAPIから受け取ったJSONを
 * このレコードへ変換し、{@code WeatherService}が
 * {@link CurrentWeather}から気温、天気コード、風速、
 * 降水量などを取り出す。
 *
 * <p>JSONの{@code current}オブジェクトは、
 * 内部レコードの{@link CurrentWeather}へ対応付けられる。
 *
 * @param latitude  天気情報を取得した地点の緯度
 * @param longitude 天気情報を取得した地点の経度
 * @param current   現在の天気情報
 */
public record OpenMeteoResponse(
        double latitude,
        double longitude,
        CurrentWeather current
) {

    /**
     * Open-Meteo APIから返される現在の天気情報を表す。
     *
     * <p>対象時刻、地上2メートル地点の気温、
     * 天気の状態を表す数値コード、
     * 地上10メートル地点の風速に加えて、
     * 降水量、雨量、にわか雨の量を保持する。
     *
     * <p>Open-Meteo APIのJSONでは、
     * {@code temperature_2m}のようなスネークケースの名前が使われる。
     * Java側の要素名と異なる項目には{@link JsonProperty}を指定し、
     * JSONのプロパティとJavaの要素を対応付けている。
     *
     * <p>降水に関する値は、天気コードだけでは実際の降水状況を
     * 適切に表せない場合に、{@code WeatherService}が
     * 表示する天候を補正するために使用する。
     *
     * @param time          対象地点の現地時刻を表す文字列
     * @param temperature   地上2メートル地点の現在の気温
     * @param weatherCode   天気の状態を表すOpen-Meteoの数値コード
     * @param windSpeed     地上10メートル地点の現在の風速
     * @param precipitation 雨、雪、にわか雨などを含む現在の総降水量
     * @param rain          現在の雨による降水量
     * @param showers       現在のにわか雨による降水量
     */
    public record CurrentWeather(
            /*
             * JSONのプロパティ名も「time」であるため、
             * JsonPropertyを指定しなくても自動的に対応付けられる。
             *
             * WeatherServiceでは、この文字列を日時へ変換し、
             * 都市のタイムゾーンを持つ現地時刻として処理する。
             */
            String time,

            /*
             * JSONの「temperature_2m」を、
             * Java側のtemperatureへ対応付ける。
             */
            @JsonProperty("temperature_2m")
            double temperature,

            /*
             * JSONの「weather_code」を、
             * Java側のweatherCodeへ対応付ける。
             *
             * WeatherServiceでは、この数値をWeatherConditionへ渡し、
             * 「晴れ」や「雨」などの説明へ変換する。
             */
            @JsonProperty("weather_code")
            int weatherCode,

            /*
             * JSONの「wind_speed_10m」を、
             * Java側のwindSpeedへ対応付ける。
             */
            @JsonProperty("wind_speed_10m")
            double windSpeed,

            /*
             * 雨、雪、にわか雨などを合計した総降水量。
             *
             * JSON側もJava側もプロパティ名が「precipitation」で
             * 一致するため、JsonPropertyの指定は不要。
             */
            double precipitation,

            /*
             * 総降水量のうち、雨による降水量。
             *
             * WeatherServiceでは、雨が実際に降っているかを
             * 判断するために使用する。
             */
            double rain,

            /*
             * にわか雨による降水量。
             *
             * rainとは別の項目として返されるため、
             * 降水状況を判断する際には両方の値を確認する。
             */
            double showers
    ) {
    }
}