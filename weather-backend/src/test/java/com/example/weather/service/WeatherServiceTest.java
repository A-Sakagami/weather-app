package com.example.weather.service;

import com.example.weather.client.OpenMeteoClient;
import com.example.weather.model.GeocodingResponse;
import com.example.weather.model.OpenMeteoResponse;
import com.example.weather.model.WeatherResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * WeatherServiceの天気情報取得処理を確認する単体テスト。
 *
 * <p>外部APIとの通信を行うOpenMeteoClientと、
 * 都市検索を行うGeocodingServiceはモックへ置き換える。
 *
 * <p>これにより、実際の通信結果に影響されず、
 * WeatherServiceが依存クラスを正しく呼び出し、
 * WeatherResponseを生成することを確認する。
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    /**
     * Open-Meteo APIとの通信を置き換えるモック。
     */
    @Mock
    private OpenMeteoClient openMeteoClient;

    /**
     * 都市検索処理を置き換えるモック。
     */
    @Mock
    private GeocodingService geocodingService;

    /**
     * テスト対象のWeatherService。
     */
    private WeatherService weatherService;

    /**
     * 各テストの実行前にWeatherServiceを生成する。
     */
    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(
                openMeteoClient,
                geocodingService
        );
    }

    /**
     * 日本の都市では気象庁MSMモデルが指定され、
     * 取得結果からWeatherResponseが生成されることを確認する。
     */
    @Test
    @DisplayName("日本の都市では気象庁MSMを使用して天気情報を返す")
    void returnsWeatherUsingJmaMsmForJapaneseCity() {
        GeocodingResponse.Result location =
                new GeocodingResponse.Result(
                        "東京",
                        35.6895,
                        139.6917,
                        "日本",
                        "東京都",
                        "Asia/Tokyo"
                );

        OpenMeteoResponse.CurrentWeather current =
                new OpenMeteoResponse.CurrentWeather(
                        "2026-08-10T12:30",
                        30.5,
                        3,
                        4.2,
                        0.5,
                        0.0,
                        0.5
                );

        OpenMeteoResponse apiResponse =
                new OpenMeteoResponse(
                        35.6895,
                        139.6917,
                        current
                );

        /*
         * getWeather(String)では国名がnullとして
         * GeocodingServiceへ渡される。
         * when: モックの振る舞いを定義する。searchCity()が呼ばれた場合、locationを返す。
         */
        when(geocodingService.searchCity("東京", null))
                .thenReturn(location);

        /*
         * 日本の都市なので、第4引数のuseJmaMsmは
         * trueになることを前提とする。
         */
        when(openMeteoClient.getCurrentWeather(
                35.6895,
                139.6917,
                "Asia/Tokyo",
                true
        )).thenReturn(apiResponse);

        WeatherResponse actual =
                weatherService.getWeather("東京");

        /*
         * にわか雨量が0より大きいため、
         * 天気コード3（曇り）より優先して
         * 天候説明が「にわか雨」になる。
         * assertAll: すべてのアサーションをまとめて実行し、失敗した場合はすべての失敗を報告する。
         * アサーションとは: 期待値と実際の値が一致するかを検証するメソッド
         */
        assertAll(
                () -> assertEquals("success", actual.status()),
                () -> assertEquals("東京", actual.city()),
                () -> assertEquals("東京都", actual.prefecture()),
                () -> assertEquals("日本", actual.country()),
                () -> assertEquals(30.5, actual.temperature()),
                () -> assertEquals(3, actual.weatherCode()),
                () -> assertEquals(
                        "にわか雨",
                        actual.weatherDescription()
                ),
                () -> assertEquals(4.2, actual.windSpeed()),
                () -> assertEquals(
                        OffsetDateTime.parse(
                                "2026-08-10T12:30+09:00"
                        ),
                        actual.time()
                )
        );

        /*
         * 都市検索で得た緯度、経度、タイムゾーンとともに、
         * 気象庁MSMを使用する指定が渡されたことを確認する。
         * verify: OpenMeteoClient.getCurrentWeather()が呼ばれたことを検証する。
         */
        verify(openMeteoClient).getCurrentWeather(
                35.6895,
                139.6917,
                "Asia/Tokyo",
                true
        );
    }

    /**
     * 日本以外の都市では、
     * 気象庁MSMモデルを使用しないことを確認する。
     */
    @Test
    @DisplayName("海外の都市では気象庁MSMを使用しない")
    void doesNotUseJmaMsmForForeignCity() {
        GeocodingResponse.Result location =
                new GeocodingResponse.Result(
                        "London",
                        51.5074,
                        -0.1278,
                        "United Kingdom",
                        "England",
                        "Europe/London"
                );

        OpenMeteoResponse.CurrentWeather current =
                new OpenMeteoResponse.CurrentWeather(
                        "2026-08-10T09:00",
                        18.0,
                        3,
                        3.5,
                        0.0,
                        0.0,
                        0.0
                );

        OpenMeteoResponse apiResponse =
                new OpenMeteoResponse(
                        51.5074,
                        -0.1278,
                        current
                );

        when(geocodingService.searchCity(
                "London",
                "United Kingdom"
        )).thenReturn(location);

        /*
         * 国名が「日本」ではないため、
         * useJmaMsmにはfalseが渡される。
         */
        when(openMeteoClient.getCurrentWeather(
                51.5074,
                -0.1278,
                "Europe/London",
                false
        )).thenReturn(apiResponse);

        WeatherResponse actual =
                weatherService.getWeather(
                        "London",
                        "United Kingdom"
                );

        assertAll(
                () -> assertEquals("London", actual.city()),
                () -> assertEquals(
                        "United Kingdom",
                        actual.country()
                ),
                () -> assertEquals(
                        "曇り",
                        actual.weatherDescription()
                )
        );

        verify(openMeteoClient).getCurrentWeather(
                51.5074,
                -0.1278,
                "Europe/London",
                false
        );
    }

    /**
     * OpenMeteoClientからnullが返された場合、
     * 明示的な例外が発生することを確認する。
     */
    @Test
    @DisplayName("天気APIのレスポンスがnullの場合は例外を投げる")
    void throwsExceptionWhenApiResponseIsNull() {
        GeocodingResponse.Result location =
                new GeocodingResponse.Result(
                        "東京",
                        35.6895,
                        139.6917,
                        "日本",
                        "東京都",
                        "Asia/Tokyo"
                );

        when(geocodingService.searchCity("東京", null))
                .thenReturn(location);

        when(openMeteoClient.getCurrentWeather(
                35.6895,
                139.6917,
                "Asia/Tokyo",
                true
        )).thenReturn(null);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> weatherService.getWeather("東京")
                );

        assertEquals(
                "天気情報の形式が正しくありません",
                exception.getMessage()
        );
    }

    /**
     * APIレスポンスは存在していても、
     * currentがnullの場合は例外が発生することを確認する。
     */
    @Test
    @DisplayName("現在の天気情報がnullの場合は例外を投げる")
    void throwsExceptionWhenCurrentWeatherIsNull() {
        GeocodingResponse.Result location =
                new GeocodingResponse.Result(
                        "東京",
                        35.6895,
                        139.6917,
                        "日本",
                        "東京都",
                        "Asia/Tokyo"
                );

        OpenMeteoResponse apiResponse =
                new OpenMeteoResponse(
                        35.6895,
                        139.6917,
                        null
                );

        when(geocodingService.searchCity("東京", null))
                .thenReturn(location);

        when(openMeteoClient.getCurrentWeather(
                35.6895,
                139.6917,
                "Asia/Tokyo",
                true
        )).thenReturn(apiResponse);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> weatherService.getWeather("東京")
                );

        assertEquals(
                "天気情報の形式が正しくありません",
                exception.getMessage()
        );
    }
}