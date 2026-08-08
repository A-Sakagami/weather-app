package com.example.weather.controller;

import com.example.weather.model.City;
import com.example.weather.model.CityResponse;
import com.example.weather.model.WeatherResponse;
import com.example.weather.service.WeatherService;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天気情報に関するHTTPリクエストを受け付けるController。
 *
 * <p>Controllerは、ブラウザなどから送られてきたリクエストを受け取り、
 * 必要な値をServiceへ渡し、その処理結果をレスポンスとして返す役割を持つ。
 *
 * <p>地名検索や天気情報の取得といった具体的な処理は、
 * Controllerでは行わず{@link WeatherService}へ任せる。
 * これにより、HTTP通信を扱う処理と、天気情報を取得する処理を分離している。
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    /**
     * 地名検索と天気情報の取得を担当するService。
     *
     * <p>{@code final}にすることで、コンストラクタで設定された後に
     * 別のインスタンスへ変更されないようにしている。
     */
    private final WeatherService weatherService;

    /**
     * WeatherControllerを生成する。
     *
     * <p>Springが管理しているWeatherServiceのインスタンスが、
     * コンストラクタの引数へ自動的に渡される。
     * この方法をコンストラクタインジェクションという。
     *
     * <p>Controller自身がWeatherServiceを生成しないため、
     * クラス同士の結び付きが弱くなり、テストや将来の変更がしやすくなる。
     *
     * @param weatherService 天気情報の取得処理を担当するService
     */
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * 指定された都市の現在の天気情報を取得する。
     *
     * <p>{@code GET /api/weather}へのリクエストを処理する。
     * 例えば、次のURLでは「北見」の天気を取得する。
     *
     * <pre>{@code
     * /api/weather?city=北見
     * }</pre>
     *
     * <p>{@code country}は同名都市を国名で絞り込むための任意項目である。
     * Controllerは受け取った値をWeatherServiceへ渡し、
     * 地名検索や外部API通信そのものはServiceに任せる。
     *
     * @param city    検索する都市名。省略された場合はTOKYOを使用する
     * @param country 検索対象の国名。指定しない場合はnullになる
     * @return 都市名、気温、天気、風速などを含む現在の天気情報
     */
    @GetMapping
    public WeatherResponse getWeather(
            @RequestParam(defaultValue = "TOKYO") String city,
            @RequestParam(required = false) String country
    ) {
        return weatherService.getWeather(city, country);
    }

    //     @GetMapping
    // public WeatherResponse getWeather(
    //         @RequestParam(defaultValue = "TOKYO") String city
    // ) {
    //     City selectedCity = City.fromName(city);
    //     return weatherService.getWeather(selectedCity);
    // }

    /**
     * City enumに登録されている都市の一覧を取得する。
     *
     * <p>{@code GET /api/weather/cities}へのリクエストを処理する。
     *
     * <p>{@link City#values()}で全都市を取得し、
     * APIで返すための{@link CityResponse}へ変換する。
     * enumをそのまま返さないことで、外部へ公開するデータの形式を
     * Controller側で明確にしている。
     *
     * @return 都市コードと画面表示名を持つ都市一覧
     */
    @GetMapping("/cities")
    public List<CityResponse> getCities() {
        return Arrays.stream(City.values())
                .map(city -> new CityResponse(
                        city.name(),
                        city.getDisplayName()
                ))
                .toList();
    }
}