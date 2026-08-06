package com.kbs0000.project.infra.shared.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(GlobalExceptionHandler.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(GlobalExceptionHandler exception) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        LocalDateTime.now(),
                        exception.getStatus().value(),
                        exception.getMessage()
                ),
                exception.getStatus()
        );
    }

}
