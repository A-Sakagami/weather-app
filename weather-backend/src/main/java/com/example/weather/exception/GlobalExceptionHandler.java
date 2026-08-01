package com.example.weather.exception;

import com.example.weather.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

/**W
 * アプリケーション全体で発生する例外を処理し、適切なHTTPレスポンスを返すためのグローバル例外ハンドラー
 * 
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    //  IllegalArgumentExceptionを処理する例外ハンドラー
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "error",
                exception.getMessage(),
                LocalDateTime.now()
        );
        // HTTPステータスコード400 Bad Requestを返す
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    //  RestClientExceptionを処理する例外ハンドラー
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleRestClientException(
            RestClientException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "error",
                "天気情報の取得に失敗しました",
                LocalDateTime.now()
        );
        // HTTPステータスコード502 Bad Gatewayを返す
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(response);
    }

    // IllegalStateExceptionを処理する例外ハンドラー
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
        IllegalStateException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "error",
                exception.getMessage(),
                LocalDateTime.now()
        );
        // HTTPステータスコード502 Bad Gatewayを返す
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(response);
    }
}