package com.example.weather.service;

import com.example.weather.client.OpenMeteoClient;
import com.example.weather.model.GeocodingResponse;
import com.example.weather.model.OpenMeteoResponse;
import com.example.weather.model.WeatherCondition;
import com.example.weather.model.WeatherResponse;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

/**
 * 都市名から現在の天気情報を取得し、
 * 画面へ返すレスポンスを組み立てるService。
 *
 * <p>都市名だけでは天気APIを呼び出せないため、最初に
 * {@link GeocodingService}を使って緯度・経度・タイムゾーンを取得する。
 * 取得した位置情報を{@link OpenMeteoClient}へ渡し、
 * Open-Meteoから現在の天気情報を取得する。
 *
 * <p>Controllerと外部APIのClientを直接つながず、
 * このServiceで一連の処理を組み合わせることで、
 * HTTPリクエストの受付、処理の判断、外部通信の役割を分離している。
 */
@Service
public class WeatherService {

    /**
     * 緯度・経度を使ってOpen-Meteoから天気情報を取得するClient。
     */
    private final OpenMeteoClient openMeteoClient;

    /**
     * 都市名から緯度・経度・タイムゾーンなどを検索するService。
     */
    private final GeocodingService geocodingService;

    /**
     * WeatherServiceを生成する。
     *
     * <p>Springが管理しているOpenMeteoClientとGeocodingServiceが、
     * コンストラクタの引数へ自動的に渡される。
     *
     * <p>必要なクラスを外部から受け取るコンストラクタインジェクションにより、
     * WeatherServiceが依存先を直接生成せずに利用できる。
     *
     * @param openMeteoClient   Open-Meteoから天気情報を取得するClient
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
     * 指定された都市の現在の天気情報を取得する。
     *
     * <p>処理は次の順序で行う。
     * 都市名から位置情報を検索し、その緯度・経度を使って
     * 現在の天気を取得した後、画面へ返すWeatherResponseを生成する。
     *
     * @param cityName 検索する都市名
     * @param country  検索対象の国名。指定されていない場合はnull
     * @return 都市情報と現在の天気情報をまとめたレスポンス
     * @throws IllegalStateException 天気APIから必要なデータを取得できなかった場合
     */
    public WeatherResponse getWeather(
            String cityName,
            String country
    ) {
        /*
         * 都市名から緯度、経度、都道府県、国、
         * タイムゾーンなどの位置情報を取得する。
         *
         * 「北見」の行政区分補完や国名による候補の絞り込みは、
         * GeocodingService側で処理される。
         */
        GeocodingResponse.Result location =
                geocodingService.searchCity(cityName, country);

        /*
         * 地名検索で得た緯度・経度を使い、
         * Open-Meteoからその地点の現在の天気を取得する。
         *
         * タイムゾーンは、対象都市の現地時刻を
         * 正しく扱うために一緒に渡している。
         */
        OpenMeteoResponse apiResponse =
                openMeteoClient.getCurrentWeather(
                        location.latitude(),
                        location.longitude(),
                        location.timezone()
                );

        /*
         * 外部APIとの通信に成功しても、レスポンス本体や
         * currentが存在しなければ天気情報を組み立てられない。
         *
         * そのまま処理を続けてNullPointerExceptionになることを防ぎ、
         * 想定外のレスポンス形式であることを明示する。
         */
        if (apiResponse == null || apiResponse.current() == null) {
            throw new IllegalStateException(
                    "天気情報の形式が正しくありません"
            );
        }

        /*
         * currentを変数へ取り出し、以降の処理で
         * apiResponse.current()を繰り返し記述しないようにする。
         */
        OpenMeteoResponse.CurrentWeather current =
                apiResponse.current();

        /*
         * Open-Meteoの数値形式の天気コードを、
         * アプリで扱うWeatherConditionへ変換する。
         *
         * これにより、フロントエンドへ
         * 「晴れ」「曇り」などの説明文を返せる。
         */
        WeatherCondition condition =
                WeatherCondition.fromCode(
                        current.weatherCode()
                );

        /*
         * Open-Meteoから返される時刻にはUTCオフセットが含まれないため、
         * まずLocalDateTimeとして読み込む。
         */
        LocalDateTime localDateTime =
                LocalDateTime.parse(current.time());

        /*
         * 地名検索で得た都市のタイムゾーンを設定し、
         * UTCオフセットを持つOffsetDateTimeへ変換する。
         *
         * これにより、東京やロンドンなど、都市ごとの時差を
         * 含んだ時刻としてレスポンスへ格納できる。
         */
        OffsetDateTime observedAt =
                localDateTime
                        .atZone(ZoneId.of(location.timezone()))
                        .toOffsetDateTime();

        /*
         * 地名検索の結果と天気APIの結果を一つにまとめ、
         * Controllerを通してJSONレスポンスとして返す。
         */
        return new WeatherResponse(
                "success",
                location.name(),
                location.admin1(),
                location.country(),
                current.temperature(),
                current.weatherCode(),
                condition.getDescription(),
                current.windSpeed(),
                observedAt
        );
    }
}