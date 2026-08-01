package com.example.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WeatherApiProperties
 * OpenMeteo APIの設定を保持するクラス。
 * このクラスは、OpenMeteo APIのベースURLとジオコーディングURLを格納するためのプロパティを提供する。
 * prefix: "weather.api"で始まるプロパティを自動的にバインドする。
 */
@Component
@ConfigurationProperties(prefix = "weather.api")
public class WeatherApiProperties {

    private String baseUrl;
    private String geocodingUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getGeocodingUrl() {
        return geocodingUrl;
    }

    public void setGeocodingUrl(String geocodingUrl) {
        this.geocodingUrl = geocodingUrl;
    }
}