package com.kbs0000.project.infra.shared.exception;

import org.springframework.http.HttpStatus;

public class DatabaseErrorException extends GlobalExceptionHandler {
    public DatabaseErrorException(String message, Throwable cause, HttpStatus status) {
        super(message, cause, status);
    }
}
