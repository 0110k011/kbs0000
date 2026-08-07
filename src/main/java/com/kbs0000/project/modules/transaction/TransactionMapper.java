package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.modules.transaction.dto.TransactionRequest;
import com.kbs0000.project.modules.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(TransactionEntity entity) {

        if (entity == null) {
            return null;
        }

        return new TransactionResponse(
                entity.getId(),
                entity.getFinancialTransactionId(),
                entity.getTransactionType().name(),
                entity.getDatePosted().toString(),
                entity.getTransactionAmount().toString(),
                entity.getMemo()
        );
    }

    public List<TransactionResponse> toResponseList(List<TransactionEntity> entities) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionEntity toEntity(TransactionRequest request) {

        if (request == null) {
            return null;
        }

        return TransactionEntity.builder()
                .transactionType(request.transactionType())
                .datePosted(request.datePosted())
                .transactionAmount(request.transactionAmount())
                .financialTransactionId(request.financialTransactionId())
                .memo(request.memo())
                .build();

    }
}
