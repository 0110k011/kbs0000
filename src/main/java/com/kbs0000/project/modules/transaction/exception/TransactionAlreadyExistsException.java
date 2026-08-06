package com.kbs0000.project.modules.transaction.exception;

import com.kbs0000.project.infra.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

public class TransactionAlreadyExistsException extends ApiException {
    public TransactionAlreadyExistsException() {
        super("Financial Transaction Id(s) already exists", HttpStatus.CONFLICT);
    }
}
