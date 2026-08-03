package com.example.weather.model;

import java.util.List;

/**
 * ジオコーディングAPIからのレスポンスを表す。これには、位置情報を含む結果のリストが含まれる。
 * @param results ジオコーディングの結果を含むリスト
 */
public record GeocodingResponse(
        List<Result> results
) {

    public record Result(
            String name,
            double latitude,
            double longitude,
            String country,
            String admin1,
            String timezone
    ) {
    }
}
