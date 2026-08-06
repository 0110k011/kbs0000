package com.kbs0000.project.infra.shared.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message
) {
}
