package com.novapay.fraud_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

// @RestControllerAdvice: intercepta exceções de todos os @RestController
// Centraliza tratamento de erros — sem try/catch nos controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FraudNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFraudNotFound(FraudNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 500,
                "error", "Internal Server Error",
                "message", "Erro interno no fraud-service"
        ));
    }
}