package com.kbs0000.project.infra.shared.exception;

import java.util.List;

public record ValidationErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        List<InvalidField> invalidFields
) {
    public record InvalidField(
            String field,
            String message
    ) {}
}
