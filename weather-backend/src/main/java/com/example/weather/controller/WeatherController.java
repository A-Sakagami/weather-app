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
 * 天候関連のAPIエンドポイントを処理するためのコントローラー。
 * @param weatherService 天候情報を取得するためのサービス
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;
    /*
    * コンストラクタの引数にWeatherServiceを書くと、Springが管理しているWeatherServiceのインスタンスを自動的に渡す。
    * これをコンストラクタインジェクションと呼ぶ。
    */
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // 天気情報を取得するAPIエンドポイント
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

    // enum Cityの値を取得するAPIエンドポイント
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