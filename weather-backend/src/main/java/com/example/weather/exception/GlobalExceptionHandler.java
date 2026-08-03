package com.example.weather.exception;

import com.example.weather.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;


/**
 * アプリケーション全体で発生する例外を処理し、適切なHTTPレスポンスを返すためのグローバル例外ハンドラー
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                "error",
                exception.getMessage(),
                OffsetDateTime.now(ZoneId.of("Asia/Tokyo"))
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(
            IllegalStateException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                "error",
                exception.getMessage(),
                OffsetDateTime.now(ZoneId.of("Asia/Tokyo"))
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    
    @ExceptionHandler(RestClientException.class)
        public ResponseEntity<ErrorResponse> handleRestClientException(
                RestClientException exception
        ) {
                ErrorResponse response = new ErrorResponse(
                        "error",
                        "天気情報の取得に失敗しました",
                        LocalDateTime.now()
                );

                return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(response);
    }

    @ExceptionHandler(CityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleCityNotFoundException(
                CityNotFoundException exception
        ) {
        ErrorResponse response = new ErrorResponse(
                "error",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
        }
}