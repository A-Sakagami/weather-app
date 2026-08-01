package com.example.weather.model;

import java.time.LocalDateTime;

/**
 * APIリクエストのエラー応答を表すレコード。
 * ErrorResponse
 * @param status
 * @param message
 * @param timestamp
 */
public record ErrorResponse(
        String status,
        String message,
        LocalDateTime timestamp
) {
}
