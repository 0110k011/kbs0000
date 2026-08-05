package com.kbs0000.project.modules.transaction.dto;

public record TransactionResponse(
        String financialTransactionId,
        String transactionType,
        String datePosted,
        String transactionAmount,
        String memo
) {
}
