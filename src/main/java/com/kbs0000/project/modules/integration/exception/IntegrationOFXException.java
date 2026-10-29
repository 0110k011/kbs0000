package com.kbs0000.project.modules.integration.exception;

import com.kbs0000.project.infra.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

public class IntegrationOFXException extends ApiException {
    public IntegrationOFXException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public IntegrationOFXException(String message, HttpStatus status, Throwable exception) {
        super(message, exception, status);
    }
}
