package com.kbs0000.project.modules.transaction.dto;

import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String financialTransactionId,
        String transactionType,
        String datePosted,
        String transactionAmount,
        String memo
) {
}
