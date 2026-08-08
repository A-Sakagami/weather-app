package com.example.weather.client;

import com.example.weather.config.WeatherApiProperties;
import com.example.weather.model.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Open-Meteo APIとのHTTP通信を担当するClient。
 *
 * <p>
 * 緯度、経度、タイムゾーンを外部APIへ送り、
 * 現在の気温、天気コード、風速を取得する。
 *
 * <p>
 * このクラスは、APIへ送るクエリパラメーターの設定と、
 * レスポンスのJavaオブジェクトへの変換を担当する。
 *
 * <p>
 * 都市名から緯度・経度を検索する処理や、
 * 取得した天気コードを説明文へ変換する処理は行わない。
 * それらの処理は、それぞれGeocodingServiceとWeatherServiceが担当する。
 */
@Component
public class OpenMeteoClient {

        /**
         * Open-Meteo APIへHTTPリクエストを送信するRestClient。
         *
         * <p>
         * APIのベースURLはコンストラクタで設定されるため、
         * getCurrentWeatherメソッドでは、緯度や経度などの
         * クエリパラメーターだけを設定すればよい。
         */
        private final RestClient restClient;

        /**
         * OpenMeteoClientを生成し、外部APIとの通信に使用する
         * RestClientを初期化する。
         *
         * <p>APIのURLをこのクラスへ直接記述せず、
         * {@link WeatherApiProperties}から取得する。
         * これにより、接続先が変更された場合でも、
         * Javaコードを修正せず設定ファイル側で対応できる。
         *
         * <p>{@code RestClient.builder()}でRestClientの設定を開始し、
         * {@code baseUrl()}でOpen-Meteo APIのURLを設定した後、
         * {@code build()}で実際に使用するRestClientを生成する。
         *
         * @param weatherApiProperties 天気APIに関する設定値を保持するクラス
         */
        public OpenMeteoClient(WeatherApiProperties weatherApiProperties) {
                this.restClient = RestClient.builder()
                                .baseUrl(weatherApiProperties.getBaseUrl())
                                .build();
        }

        /**
         * 指定された地点の現在の天気情報を取得する。
         *
         * <p>緯度、経度、取得する天気項目、風速の単位、
         * タイムゾーンをクエリパラメーターとして設定し、
         * Open-Meteo APIへGETリクエストを送信する。
         *
         * <p>取得する天気項目には、現在の気温、天気コード、
         * 地上10メートル地点の風速を指定している。
         *
         * <p>APIから返されたJSONは、RestClientによって
         * {@link OpenMeteoResponse}へ変換される。
         *
         * @param latitude  天気を取得する地点の緯度
         * @param longitude 天気を取得する地点の経度
         * @param timezone  対象地点のタイムゾーン
         * @return Open-Meteo APIから取得した現在の天気情報
        */
        public OpenMeteoResponse getCurrentWeather(
                        double latitude,
                        double longitude,
                        String timezone) {
                // Open-Meteo APIを呼び出して、指定された緯度、経度、およびタイムゾーンに基づいて現在の天気情報を取得する
                // 返却されるレスポンスはOpenMeteoResponseクラスにマッピングされる
                // APIのエンドポイントは、緯度、経度、取得する情報（temperature_2m, weather_code,
                // wind_speed_10m）、およびタイムゾーンをクエリパラメータとして指定する
                // エンドポイントの例:
                // /v1/forecast?latitude=35.6895&longitude=139.6917&current=temperature_2m,weather_code,wind_speed_10m&timezone=Asia/Tokyo
                return restClient.get()
                                /*
                                * uriBuilderを使い、コンストラクタで設定した
                                * ベースURLへクエリパラメーターを追加する。
                                */
                                .uri(uriBuilder -> uriBuilder
                                        /* 天気を取得する地点の緯度を指定する。この値は、都市名の検索結果からWeatherServiceを通して渡される。*/
                                        .queryParam("latitude", latitude)
                                        /* 天気を取得する地点の経度を指定する。*/
                                        .queryParam("longitude", longitude)
                                        /*
                                        * 現在の天気として取得する項目を指定する。
                                        *
                                        * temperature_2mは地上2メートル地点の気温、
                                        * weather_codeは天気の状態を表す数値、
                                        * wind_speed_10mは地上10メートル地点の風速を表す。
                                        */
                                        .queryParam("current",
                                                        "temperature_2m,weather_code,wind_speed_10m")
                                        /* 風速の単位をメートル毎秒に指定する。この指定により、APIから返される風速を画面上で「m/s」として表示できる。*/
                                        .queryParam("wind_speed_unit", "ms")
                                        /*
                                        * 対象都市のタイムゾーンを指定する。
                                        * autoではなく地名検索で取得したタイムゾーンを明示的に渡すことで、その都市の現地時刻を取得する。
                                        */
                                        .queryParam("timezone", timezone)
                                        .build())
                                /*
                                * HTTPリクエストを実行し、
                                * Open-Meteo APIからレスポンスを取得する。
                                *
                                * APIから4xxや5xxのHTTPエラーが返された場合は、
                                * RestClientが例外を発生させる。
                                */
                                .retrieve()
                                /*
                                * レスポンスのJSONをOpenMeteoResponseへ変換して返す。
                                *
                                * レスポンス本体やcurrentが存在するかどうかは、
                                * 呼び出し元のWeatherServiceで確認される。
                                */
                                .body(OpenMeteoResponse.class);
        }
}