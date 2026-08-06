package com.kbs0000.project.infra.shared.exception;

import org.springframework.http.HttpStatus;

public class GlobalExceptionHandler extends RuntimeException {

    private final HttpStatus status;

    protected GlobalExceptionHandler(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    protected GlobalExceptionHandler(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
