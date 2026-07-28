package com.example.weather.controller;

import com.example.weather.model.WeatherResponse;
import com.example.weather.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天候関連のAPIエンドポイントを処理するためのコントローラー。
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

    @GetMapping
    public WeatherResponse getWeather() {
        return weatherService.getWeather();
    }
}