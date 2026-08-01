package com.example.weather.model;
import java.util.Arrays;

/**
 * 都市を表す列挙型。
 * 今後の実装では、位置情報APIから取得した都市情報を使用することも検討されるが、現時点では固定の都市リストを使用する。
 * これにより、アプリケーションの初期段階での開発の簡略化を図っている。
 * 最終実装には使用しない。
 * @param displayName 都市の表示名
 * @param latitude 緯度
 * @param longitude 経度
 */
public enum City {

    TOKYO("東京", 35.6895, 139.6917),
    OSAKA("大阪", 34.6937, 135.5023),
    NAGOYA("名古屋", 35.1815, 136.9066),
    SAPPORO("札幌", 43.0618, 141.3545),
    FUKUOKA("福岡", 33.5904, 130.4017);

    private final String displayName;
    private final double latitude;
    private final double longitude;

    City(
            String displayName,
            double latitude,
            double longitude
    ) {
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // 都市名からCity列挙型を取得するメソッド。大文字小文字を区別せず、表示名でも検索可能。
    public static City fromName(String cityName) {
        return Arrays.stream(values())
                .filter(city ->
                        city.name().equalsIgnoreCase(cityName)
                                || city.displayName.equals(cityName)
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "対応していない都市です: " + cityName
                        )
                );
    }
}