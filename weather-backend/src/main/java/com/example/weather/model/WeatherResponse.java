package com.example.weather.model;

import java.time.OffsetDateTime;
/**
 * クライアントへ返す現在の天気情報を表すレスポンス。
 *
 * <p>{@code record}を使用することで、各項目を保持するフィールド、
 * コンストラクタ、値を取得するためのメソッドなどが自動的に生成される。
 *
 * <p>WeatherServiceが地名検索と天気情報の取得結果をまとめて
 * このレコードを生成し、WeatherControllerがHTTPレスポンスとして返す。
 *
 * <p>Spring Bootによって各要素がJSONのプロパティへ変換される。
 * 例えば、{@code city}はJSONの{@code "city"}として出力される。
 *
 * @param status             処理結果を表すステータス
 * @param city               検索された都市名
 * @param prefecture         都市が属する都道府県や州などの行政区分
 * @param country            都市が属する国
 * @param temperature        現在の気温（摂氏）
 * @param weatherCode        天気の状態を表すOpen-Meteoの数値コード
 * @param weatherDescription 天気コードに対応する日本語の説明
 * @param windSpeed          現在の風速（メートル毎秒）
 * @param time               都市のUTCオフセットを含む観測日時
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
        OffsetDateTime time
) {
}