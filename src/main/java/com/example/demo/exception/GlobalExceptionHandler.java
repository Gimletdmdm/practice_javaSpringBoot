package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // バリデーションエラーの例外
import org.springframework.web.bind.annotation.ControllerAdvice; // グローバルエラーハンドリングを有効にするアノテーション
import org.springframework.web.bind.annotation.ExceptionHandler; // 特定の例外を処理するメソッドに付与
import org.springframework.web.context.request.WebRequest; // リクエスト情報にアクセス
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// エラーレスポンスの構造を定義するDTO (任意だが推奨)
class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private int status;

    public ErrorDetails(LocalDateTime timestamp, String message, String details, int status) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public int getStatus() {
        return status;
    }
}

@ControllerAdvice // アプリケーション全体のエラーを処理することをSpringに伝える
public class GlobalExceptionHandler {

    // ResourceNotFoundException を処理するハンドラ
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false), // リクエストの詳細（URIなど）
                HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND); // 404 Not Found を返す
    }

    // バリデーションエラー (MethodArgumentNotValidException) を処理するハンドラ
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // 400 Bad Request を返す
    }

    // その他の予期せぬ例外を処理する汎用ハンドラ
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Internal Server Error", // 汎用的なメッセージ
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error を返す
    }
}
