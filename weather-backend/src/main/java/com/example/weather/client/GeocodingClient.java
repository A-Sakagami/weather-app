package com.example.weather.client;

import com.example.weather.config.WeatherApiProperties;
import com.example.weather.model.GeocodingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Geocoding APIとのHTTP通信を担当するClient。
 *
 * <p>都市名を外部APIへ送り、その都市の緯度、経度、
 * 都道府県、国、タイムゾーンなどを取得する。
 *
 * <p>このクラスは、リクエストURLの組み立てと
 * レスポンスのJavaオブジェクトへの変換を担当する。
 *
 * <p>検索結果からどの都市を選ぶか、行政区分を補って
 * 再検索するかといったアプリ固有の判断は行わず、
 * {@code GeocodingService}へ任せている。
 */
@Component
public class GeocodingClient {

    /**
     * Geocoding APIへHTTPリクエストを送信するRestClient。
     *
     * <p>APIのベースURLはコンストラクタで設定されるため、
     * searchメソッドではクエリパラメーターだけを指定すればよい。
     */
    private final RestClient restClient;

    /**
     * GeocodingClientを生成し、外部APIとの通信に使用する
     * RestClientを初期化する。
     *
     * <p>APIのURLをJavaコードへ直接記述せず、
     * {@link WeatherApiProperties}から取得する。
     * これにより、設定ファイル側でURLを管理でき、
     * 接続先が変わった場合もこのクラスを修正せずに対応できる。
     *
     * <p>{@code RestClient.builder()}でRestClientの設定を開始し、
     * {@code baseUrl()}でGeocoding APIの共通URLを設定した後、
     * {@code build()}で実際に使用するRestClientを生成する。
     *
     * @param weatherApiProperties 天気APIに関する設定値を保持するクラス
     */
    public GeocodingClient(WeatherApiProperties weatherApiProperties) {
        this.restClient = RestClient.builder()
                .baseUrl(weatherApiProperties.getGeocodingUrl())
                .build();
    }

    /**
     * 指定された都市名をGeocoding APIで検索する。
     *
     * <p>都市名や取得件数などをクエリパラメーターとして設定し、
     * GETリクエストを送信する。
     *
     * <p>APIから返されたJSONは、RestClientによって
     * {@link GeocodingResponse}へ変換される。
     *
     * <p>このメソッドは検索結果をそのまま返すだけであり、
     * 日本の候補を優先する処理や行政区分の補完は行わない。
     * それらの判断はGeocodingServiceが担当する。
     *
     * @param cityName 検索する都市名
     * @return Geocoding APIから取得した都市候補
     */
    public GeocodingResponse search(String cityName) {
        /*
        * uriBuilderを使い、ベースURLへ
        * Geocoding APIのクエリパラメーターを追加する。
        *
        * uriBuilderが都市名をURLで送信可能な形式へ変換するため、
        * 日本語の都市名も安全に送信できる。
        */
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        /* 検索対象の都市名を指定する。cityNameには「東京」や「北見市」など、GeocodingServiceから渡された文字列が入る。*/
                        .queryParam("name", cityName)
                        /* APIから取得する都市候補の最大件数を指定する。同じ名前の都市が複数の国や地域に存在する場合に、候補を比較できるよう5件取得する。*/
                        .queryParam("count", 5)
                        /* 都市名、都道府県名、国名などを日本語で受け取るよう指定する。*/
                        .queryParam("language", "ja")
                        /* APIレスポンスをJSON形式で受け取るよう指定する。JSONの各項目はGeocodingResponseのrecordコンポーネントへ対応付けられる。*/
                        .queryParam("format", "json")
                        /* 設定したクエリパラメーターからリクエスト先のURIを完成させる。*/
                        .build())
                /* HTTPリクエストを実行し、APIレスポンスを取得する。APIから4xxや5xxのHTTPエラーが返された場合は、RestClientが例外を発生させる。*/
                .retrieve()
                /* 
                * レスポンスのJSONを GeocodingResponseへ変換して返す。
                * APIのレスポンス本文が空の場合はnullになる可能性があるが、その確認は呼び出し元のGeocodingServiceで行われる。
                */
                .body(GeocodingResponse.class);
    }
}