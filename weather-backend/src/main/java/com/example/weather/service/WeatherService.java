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

/**
 * 都市名を基に現在の天気情報を取得するService。
 *
 * <p>都市名から位置情報を検索し、その緯度、経度、
 * タイムゾーンを使用してOpen-Meteo APIから
 * 現在の天気情報を取得する。
 *
 * <p>取得した天気コードと降水量から表示用の天候を判定し、
 * 都市情報、気温、風速、観測日時などを
 * {@link WeatherResponse}へまとめて返す。
 *
 * <p>都市検索は{@link GeocodingService}、
 * Open-Meteo APIとの通信は{@link OpenMeteoClient}、
 * 天候の判定は{@link WeatherCondition}が担当する。
 *
 * <p>このクラスでは、それぞれの処理を組み合わせ、
 * アプリとして返す天気情報を構成する。
 */
@Service
public class WeatherService {

    /**
     * Open-Meteo APIから現在の天気情報を取得するClient。
     */
    private final OpenMeteoClient openMeteoClient;

    /**
     * 都市名から緯度、経度、国、行政区分、
     * タイムゾーンなどを検索するService。
     */
    private final GeocodingService geocodingService;

    /**
     * WeatherServiceを生成する。
     *
     * <p>Springが管理しているOpenMeteoClientと
     * GeocodingServiceのインスタンスが、
     * コンストラクタの引数へ自動的に渡される。
     *
     * <p>WeatherService自身が依存するクラスを生成しないため、
     * 外部APIとの通信処理をテスト用の実装へ
     * 置き換えやすい構造になっている。
     *
     * @param openMeteoClient Open-Meteo APIとの通信を担当するClient
     * @param geocodingService 都市の位置情報を検索するService
     */
    public WeatherService(
            OpenMeteoClient openMeteoClient,
            GeocodingService geocodingService
    ) {
        this.openMeteoClient = openMeteoClient;
        this.geocodingService = geocodingService;
    }

    /**
     * 都市名から現在の天気情報を取得する。
     *
     * <p>国名を指定しない検索として、
     * {@link #getWeather(String, String)}を呼び出す。
     *
     * <p>国名が指定されていない場合の都市候補の選択は、
     * GeocodingServiceが担当する。
     * 同名の都市が複数存在する場合は、日本の都市が優先される。
     *
     * @param cityName 天気を取得する都市名
     * @return 指定された都市の現在の天気情報
     */
    public WeatherResponse getWeather(String cityName) {
        /*
         * 国名にnullを渡すことで、
         * 国を限定しない都市検索として処理する。
         */
        return getWeather(cityName, null);
    }

    /**
     * 都市名と国名から現在の天気情報を取得する。
     *
     * <p>最初にGeocodingServiceを使用して、
     * 条件に合う都市の位置情報を検索する。
     *
     * <p>検索結果の緯度、経度、タイムゾーンを
     * OpenMeteoClientへ渡し、現在の気温、天気コード、
     * 風速、降水量などを取得する。
     *
     * <p>日本の都市については、Open-Meteo APIで
     * 気象庁のMSMモデルを使用する。
     *
     * <p>天候の説明は、天気コードだけでなく、
     * 総降水量、雨量、にわか雨量も使用して判定する。
     *
     * <p>APIから返された時刻には対象都市の
     * タイムゾーンを適用し、UTCオフセットを含む
     * {@link OffsetDateTime}へ変換する。
     *
     * @param cityName 天気を取得する都市名
     * @param country  検索対象の国名。指定しない場合はnull
     * @return 指定された都市の現在の天気情報
     * @throws IllegalStateException 天気APIのレスポンスまたは
     *                               現在の天気情報が存在しない場合
     */
    public WeatherResponse getWeather(
            String cityName,
            String country
    ) {
        /*
         * 入力された都市名と国名を使用して、
         * 天気情報の取得対象となる都市を1件検索する。
         *
         * locationには都市名、緯度、経度、国、
         * 行政区分、タイムゾーンが保持される。
         */
        GeocodingResponse.Result location =
                geocodingService.searchCity(cityName, country);

        /*
         * 検索された都市が日本に属するか判定する。
         *
         * Geocoding APIから日本語で取得された国名は
         * 「日本」として保持されるため、文字列が一致する場合だけ
         * 気象庁のMSMモデルを使用する。
         */
        boolean useJmaMsm =
                "日本".equals(location.country());

        /*
         * 検索された都市の位置情報を使用して、
         * Open-Meteo APIから現在の天気情報を取得する。
         *
         * 日本の都市ではuseJmaMsmがtrueとなり、
         * OpenMeteoClientがAPIリクエストへ
         * 気象庁MSMモデルの指定を追加する。
         */
        OpenMeteoResponse apiResponse =
                openMeteoClient.getCurrentWeather(
                        location.latitude(),
                        location.longitude(),
                        location.timezone(),
                        useJmaMsm
                );

        /*
         * APIレスポンスそのもの、またはレスポンス内の
         * currentが存在するか確認する。
         *
         * 必要な情報がない状態でcurrentへアクセスすると
         * NullPointerExceptionが発生するため、
         * アプリ側で内容を確認して明示的な例外を発生させる。
         */
        if (apiResponse == null || apiResponse.current() == null) {
            throw new IllegalStateException(
                    "天気情報の形式が正しくありません"
            );
        }

        /*
         * 現在の天気情報を繰り返し参照できるよう、
         * current変数へ取り出す。
         *
         * currentには時刻、気温、天気コード、風速、
         * 総降水量、雨量、にわか雨量が保持されている。
         */
        OpenMeteoResponse.CurrentWeather current =
                apiResponse.current();

        /*
         * 天気コードと現在の降水量を使用して、
         * 画面へ表示する天候を判定する。
         *
         * にわか雨量、雨量、総降水量が記録されている場合は、
         * 天気コードだけでなく実際の降水状況も考慮される。
         */
        WeatherCondition condition =
                WeatherCondition.fromCurrentWeather(
                        current.weatherCode(),
                        current.precipitation(),
                        current.rain(),
                        current.showers()
                );

        /*
         * Open-Meteo APIの時刻文字列を、
         * タイムゾーンを持たないLocalDateTimeとして解析する。
         *
         * その後、地名検索で得た都市のタイムゾーンを設定し、
         * UTCオフセットを持つOffsetDateTimeへ変換する。
         *
         * これにより、東京やロンドンなど、都市ごとの時差を
         * 含んだ時刻としてレスポンスへ格納できる。
         */
        OffsetDateTime observedAt =
                LocalDateTime.parse(current.time())
                        .atZone(ZoneId.of(location.timezone()))
                        .toOffsetDateTime();

        /*
         * 地名検索の結果と天気APIの結果をまとめ、
         * クライアントへ返すWeatherResponseを生成する。
         */
        return new WeatherResponse(
                /*
                 * 天気情報を正常に取得できたことを表すステータス。
                 */
                "success",

                /*
                 * Geocoding APIで検索された都市名。
                 */
                location.name(),

                /*
                 * 都道府県や州など、都市が属する行政区分。
                 */
                location.admin1(),

                /*
                 * 都市が属する国。
                 */
                location.country(),

                /*
                 * 地上2メートル地点の現在の気温。
                 */
                current.temperature(),

                /*
                 * Open-Meteo APIから取得した数値の天気コード。
                 */
                current.weatherCode(),

                /*
                 * 天気コードと降水量から判定した
                 * 画面表示用の日本語説明。
                 */
                condition.getDescription(),

                /*
                 * 地上10メートル地点の現在の風速。
                 */
                current.windSpeed(),

                /*
                 * 対象都市のUTCオフセットを含む観測日時。
                 */
                observedAt
        );
    }
}