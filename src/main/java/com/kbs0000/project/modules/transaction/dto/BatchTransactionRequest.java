package com.kbs0000.project.modules.transaction.dto;

import java.util.List;

public record BatchTransactionRequest(
        List<TransactionRequest> transactions
) {
}
