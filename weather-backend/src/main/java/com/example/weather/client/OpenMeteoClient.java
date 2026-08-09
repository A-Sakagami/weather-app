package com.example.weather.client;

import com.example.weather.config.WeatherApiProperties;
import com.example.weather.model.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Open-Meteo APIとのHTTP通信を担当するClient。
 *
 * <p>緯度、経度、タイムゾーンを外部APIへ送り、
 * 現在の気温、天気コード、風速、降水量を取得する。
 *
 * <p>日本の都市を検索した場合は、気象庁のMSMモデルを
 * 使用するためのクエリパラメーターも設定する。
 *
 * <p>このクラスは、リクエスト先URIの組み立てと、
 * APIレスポンスのJavaオブジェクトへの変換を担当する。
 *
 * <p>都市名から位置情報を検索する処理や、
 * 取得した天気情報から表示する天候を判断する処理は行わない。
 * それらはGeocodingServiceとWeatherServiceが担当する。
 */
@Component
public class OpenMeteoClient {

    /**
     * Open-Meteo APIへHTTPリクエストを送信するRestClient。
     *
     * <p>APIのベースURLはコンストラクタで設定されるため、
     * getCurrentWeatherメソッドでは緯度や経度などの
     * クエリパラメーターだけを設定する。
     */
    private final RestClient restClient;

    /**
     * OpenMeteoClientを生成し、外部APIとの通信に使用する
     * RestClientを初期化する。
     *
     * <p>APIのURLをJavaコードへ直接記述せず、
     * {@link WeatherApiProperties}から取得する。
     * これにより、接続先が変更された場合でも、
     * Javaコードを修正せず設定ファイル側で対応できる。
     *
     * <p>{@code RestClient.builder()}で設定を開始し、
     * {@code baseUrl()}でOpen-Meteo APIの共通URLを設定した後、
     * {@code build()}でRestClientを生成する。
     *
     * @param weatherApiProperties 天気APIに関する設定値を保持するクラス
     */
    public OpenMeteoClient(
            WeatherApiProperties weatherApiProperties
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(
                        weatherApiProperties.getBaseUrl()
                )
                .build();
    }

    /**
     * 指定された地点の現在の天気情報を取得する。
     *
     * <p>緯度、経度、取得する天気項目、タイムゾーンを
     * クエリパラメーターとして設定し、
     * Open-Meteo APIへGETリクエストを送信する。
     *
     * <p>現在の天気として、気温、天気コード、風速に加えて、
     * 降水量、雨量、にわか雨の量を取得する。
     * 降水に関する値は、WeatherServiceで天候を判断する際に使用される。
     *
     * <p>{@code useJmaMsm}がtrueの場合は、
     * 気象庁のMSMモデルを使用するようAPIへ明示する。
     * falseの場合はモデルを指定せず、Open-Meteo側の
     * 通常のモデル選択に任せる。
     *
     * <p>APIから返されたJSONは、RestClientによって
     * {@link OpenMeteoResponse}へ変換される。
     *
     * @param latitude  天気を取得する地点の緯度
     * @param longitude 天気を取得する地点の経度
     * @param timezone  対象地点のタイムゾーン
     * @param useJmaMsm 気象庁のMSMモデルを使用する場合はtrue
     * @return Open-Meteo APIから取得した現在の天気情報
     */
    public OpenMeteoResponse getCurrentWeather(
            double latitude,
            double longitude,
            String timezone,
            boolean useJmaMsm
    ) {
        return restClient.get()

                /*
                 * uriBuilderを使い、コンストラクタで設定した
                 * ベースURLへクエリパラメーターを追加する。
                 *
                 * モデルの指定は条件によって変わるため、
                 * ラムダ式のブロック内でURIを段階的に組み立てる。
                 */
                .uri(uriBuilder -> {
                    uriBuilder

                            /*
                             * 天気を取得する地点の緯度を指定する。
                             *
                             * この値は、都市名の検索結果から
                             * WeatherServiceを通して渡される。
                             */
                            .queryParam(
                                    "latitude",
                                    latitude
                            )

                            /*
                             * 天気を取得する地点の経度を指定する。
                             */
                            .queryParam(
                                    "longitude",
                                    longitude
                            )

                            /*
                             * 現在の天気として取得する項目を指定する。
                             *
                             * temperature_2mは地上2メートル地点の気温、
                             * weather_codeは天気の状態を表す数値、
                             * wind_speed_10mは地上10メートル地点の風速を表す。
                             *
                             * precipitationは降水量全体、
                             * rainは雨による降水量、
                             * showersはにわか雨による降水量を表す。
                             *
                             * 降水に関する値を天気コードと併用することで、
                             * 実際に降水がある場合の表示を補正できる。
                             */
                            .queryParam(
                                    "current",
                                    "temperature_2m,"
                                            + "weather_code,"
                                            + "wind_speed_10m,"
                                            + "precipitation,"
                                            + "rain,"
                                            + "showers"
                            )

                            /*
                             * 対象都市のタイムゾーンを指定する。
                             *
                             * 地名検索で取得したタイムゾーンを
                             * 明示的に渡すことで、その都市の現地時刻を取得する。
                             */
                            .queryParam(
                                    "timezone",
                                    timezone
                            );

                    /*
                     * 日本の都市の場合だけ、使用する気象モデルとして
                     * 気象庁のMSMモデルを指定する。
                     *
                     * useJmaMsmは、WeatherServiceが地名検索結果の
                     * 国名を確認して決定する。
                     */
                    if (useJmaMsm) {
                        uriBuilder.queryParam(
                                "models",
                                "jma_msm"
                        );
                    }

                    /*
                     * 設定したクエリパラメーターから
                     * リクエスト先のURIを完成させる。
                     */
                    return uriBuilder.build();
                })

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