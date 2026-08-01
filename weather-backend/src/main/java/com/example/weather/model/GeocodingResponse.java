package com.example.weather.model;

import java.util.List;

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
