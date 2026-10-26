package com.kbs0000.project.modules.transaction.exception;

import com.kbs0000.project.infra.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

public class OFXException extends ApiException {
    public OFXException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
