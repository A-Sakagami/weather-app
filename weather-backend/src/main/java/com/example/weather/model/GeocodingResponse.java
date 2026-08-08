package com.example.weather.model;

import java.util.List;

/**
 * Geocoding APIから返される都市検索結果を表すレスポンス。
 *
 * <p>
 * APIから返されたJSONの{@code results}を、
 * 都市候補を表す{@link Result}のリストとして保持する。
 *
 * <p>
 * {@code GeocodingClient}がAPIレスポンスをこのレコードへ変換し、
 * {@code GeocodingService}が検索結果の有無を確認したうえで、
 * 国などの条件に合う都市候補を選択する。
 *
 * <p>
 * 検索結果が存在しない場合、APIのレスポンスによっては
 * {@code results}がnullになる可能性があるため、
 * 利用する前にGeocodingServiceで確認している。
 *
 * @param results 検索条件に一致した都市候補の一覧
 */
public record GeocodingResponse(List<Result> results) {
        /**
         * Geocoding APIから返される1件の都市候補を表す。
         *
         * <p>
         * 都市名に加えて、天気情報の取得に必要な緯度・経度と、
         * 表示や現地時刻の計算に使用する国、行政区分、
         * タイムゾーンを保持する。
         *
         * <p>
         * 緯度、経度、タイムゾーンは、
         * WeatherServiceを通してOpenMeteoClientへ渡される。
         * 都市名、国、行政区分はWeatherResponseの生成に使用される。
         *
         * @param name      APIで検索された都市名
         * @param latitude  都市の緯度
         * @param longitude 都市の経度
         * @param country   都市が属する国
         * @param admin1    都市が属する都道府県や州などの行政区分
         * @param timezone  都市が属する地域のタイムゾーン
         */
        public record Result(
                        String name,
                        double latitude,
                        double longitude,
                        String country,
                        String admin1,
                        String timezone) {
        }
}
