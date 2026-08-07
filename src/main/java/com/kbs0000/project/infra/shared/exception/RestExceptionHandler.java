package com.kbs0000.project.infra.shared.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {

        List<ValidationErrorResponse.InvalidField> invalidFields = exception.getConstraintViolations().stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();
                    String fieldName = propertyPath.contains(".")
                            ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                            : propertyPath;
                    return new ValidationErrorResponse.InvalidField(
                            fieldName,
                            violation.getMessage()
                    );
                })
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                String.format("Validation failed with %d error(s)", invalidFields.size()),
                invalidFields
        ));
    }

}
