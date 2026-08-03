package com.example.weather.exception;

/**
 * 例外クラス。指定された都市が見つからなかった場合にスローされる。
 * CityNotFoundException
 */
public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String cityName) {
        super("指定された都市が見つかりません: " + cityName);
    }
}