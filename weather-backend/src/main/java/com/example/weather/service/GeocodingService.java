package com.example.weather.service;

import com.example.weather.client.GeocodingClient;
import com.example.weather.exception.CityNotFoundException;
import com.example.weather.model.GeocodingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ジオコーディング関連の処理を行うためのサービスクラス。
 * GeocodingService
 * @apiNote このサービスは、指定された都市名に基づいて、緯度、経度、およびタイムゾーンを取得するためにGeocodingClientを使用する。
 * @param geocodingClient Geocoding APIクライアント
 */
@Service
public class GeocodingService {

    private final GeocodingClient geocodingClient;

    public GeocodingService(GeocodingClient geocodingClient) {
        this.geocodingClient = geocodingClient;
    }

    public GeocodingResponse.Result searchCity(String cityName, String country) {
        // 都市名が空でないことを確認
        if (cityName == null || cityName.isBlank()) {
            throw new IllegalArgumentException(
                    "都市名を入力してください"
            );
        }

        // GeocodingClientを使用して、指定された都市名のジオコーディング情報を取得する
        GeocodingResponse response =
                geocodingClient.search(cityName);

        // 都市が見つからなかった場合、CityNotFoundExceptionをスローする
        if (response == null
                || response.results() == null
                || response.results().isEmpty()) {
            throw new CityNotFoundException(cityName);
        }

        List<GeocodingResponse.Result> results =
                response.results();

        // 国名検索
        if (country != null && !country.isBlank()) {
            return results.stream()
                    .filter(result ->
                            country.equalsIgnoreCase(result.country())
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new CityNotFoundException(
                                    cityName + "（" + country + "）"
                            )
                    );
        }

        
        // 日本の都市を優先的に返す
        return results.stream()
                .filter(result -> "日本".equals(result.country()))
                .findFirst()
                .orElse(results.getFirst() /* =get(0) */);
    }
}
