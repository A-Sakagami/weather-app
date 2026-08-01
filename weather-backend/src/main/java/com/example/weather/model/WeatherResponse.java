package com.example.weather.model;

/**
 * 気象情報の応答を表すレコード。
 * @param status 成功または失敗のステータスを示す文字列。
 * @param city 都市名を示す文字列。
 * @param prefecture 都道府県名を示す文字列。
 * @param country 国名を示す文字列。
 * @param temperature 現在の気温を示す数値。
 * @param weatherCode 天候コードを示す整数。
 * @param weatherDescription 天候の説明を示す文字列。
 * @param windSpeed 現在の風速を示す数値。
 * @param observedAt 天気情報が観測された日時を示す文字列。
 */
public record WeatherResponse(
        String status,
        String city,
        String prefecture,
        String country,
        double temperature,
        int weatherCode,
        String weatherDescription,
        double windSpeed,
        String observedAt
) {
}