package com.kbs0000.project.infra.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {

        if (exception.getStatus().is5xxServerError()) {
            log.error("Internal server error: {}", exception.getMessage(), exception);
        } else {
            log.warn("Client error({}): {}", exception.getStatus(), exception.getMessage());
        }

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
