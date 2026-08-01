package com.example.weather.service;

import com.example.weather.client.GeocodingClient;
import com.example.weather.model.GeocodingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeocodingService {

    private final GeocodingClient geocodingClient;

    public GeocodingService(GeocodingClient geocodingClient) {
        this.geocodingClient = geocodingClient;
    }

    public GeocodingResponse.Result searchCity(String cityName) {
        // 都市名が空でないことを確認
        if (cityName == null || cityName.isBlank()) {
            throw new IllegalArgumentException(
                    "都市名を入力してください"
            );
        }

        // GeocodingClientを使用して、指定された都市名のジオコーディング情報を取得する
        GeocodingResponse response =
                geocodingClient.search(cityName);

        if (response == null
                || response.results() == null
                || response.results().isEmpty()) {
            throw new IllegalArgumentException(
                    "都市が見つかりませんでした: " + cityName
            );
        }

        List<GeocodingResponse.Result> results =
                response.results();
        
        // 日本の都市を優先的に返す
        return results.stream()
                .filter(result -> "日本".equals(result.country()))
                .findFirst()
                .orElse(results.getFirst() /* =get(0) */);
    }
}
