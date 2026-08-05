package com.kbs0000.project.modules.transaction.dto;

import com.kbs0000.project.modules.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(

    @NotNull(message = "Transaction type is required")
    TransactionType transactionType,

    @NotNull(message = "Date posted is required")
    LocalDateTime datePosted,

    @NotNull(message = "Transaction amount is required")
    BigDecimal transactionAmount,

    @NotBlank(message = "Financial transaction ID is required")
    String financialTransactionId,

    @NotBlank(message = "Memo is required")
    String memo

) {}
