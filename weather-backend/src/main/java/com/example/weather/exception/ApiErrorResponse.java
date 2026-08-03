package com.example.weather.exception;

import java.time.OffsetDateTime;

/**
 * APIエラーのレスポンスを表すレコード。これには、エラーのステータス、メッセージ、およびタイムスタンプが含まれる。
 * @param status エラーのステータス（例："error"）。
 * @param message エラーの詳細なメッセージ。
 * @param timestamp エラーが発生した日時。
 */
public record ApiErrorResponse(
        String status,
        String message,
        OffsetDateTime timestamp
) {
}