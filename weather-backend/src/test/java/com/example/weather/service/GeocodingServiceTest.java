package com.example.weather.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.weather.client.GeocodingClient;
import com.example.weather.model.GeocodingResponse;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    /**
     * 外部APIとの通信部分。
     *
     * 単体テストでは実際のOpen-Meteo APIを呼び出さず、
     * Mockitoを使用して検索結果を固定する。
     */
    @Mock
    private GeocodingClient geocodingClient;

    /**
     * テスト対象。
     *
     * GeocodingService自体はモックせず、
     * 実際の処理をテストする。
     */
    private GeocodingService geocodingService;

    /**
     * 各テストの実行前にGeocodingServiceを生成する。
     */
    @BeforeEach
    void setUp() {
        geocodingService = new GeocodingService(geocodingClient);
    }

    /**
     * 「北見」を検索した場合、
     * GeocodingClientが返した日本の北見市を選択できることを確認する。
     */
    @Test
    @DisplayName("北見を検索すると日本の北見市が取得できる")
    void searchCity_Kitami() {

        /*
         * 実際のAPIレスポンスの代わりとなる検索結果を作る。
         *
         * GeocodingResponseは普通のrecordなので、
         * モックせず実際のオブジェクトを使用する。
         */
        GeocodingResponse response =
                new GeocodingResponse(
                        List.of(
                                new GeocodingResponse.Result(
                                        "北見市",
                                        43.8040,
                                        143.8950,
                                        "日本",
                                        "北海道",
                                        "Asia/Tokyo"
                                )
                        )
                );

        /*
         * GeocodingClient.search("北見") が呼ばれた場合、
         * 上で作ったレスポンスを返す。
         */
        when(geocodingClient.search("北見"))
                .thenReturn(response);

        /*
         * GeocodingServiceの実際の処理を実行する。
         */
        GeocodingResponse.Result result =
                geocodingService.searchCity("北見", null);

        /*
         * Serviceが正しい候補を返したことを確認する。
         */
        assertEquals("北見市", result.name());
        assertEquals("日本", result.country());
        assertEquals("北海道", result.admin1());
    }

    /**
     * 「美幌」を検索した場合、
     * GeocodingClientが返す候補の中に美幌町が存在しないため、
     * GeocodingServiceが例外登録された美幌を返すことを確認する。
     */
    @Test
    @DisplayName("美幌を検索すると例外登録された美幌町が取得できる")
    void searchCity_Bihoro() {

        GeocodingResponse.Result result =
                geocodingService.searchCity("美幌", null);

        assertEquals("美幌", result.name());
        assertEquals("日本", result.country());
        assertEquals("北海道", result.admin1());

        verifyNoInteractions(geocodingClient);
    }

    @Test
    @DisplayName("津別を検索すると例外登録された津別町が取得できる")
    void searchCity_Tsubetsu() {

        GeocodingResponse.Result result =
                geocodingService.searchCity("津別", null);

        assertEquals("津別", result.name());
        assertEquals("日本", result.country());
        assertEquals("北海道", result.admin1());

        verifyNoInteractions(geocodingClient);
    }
}