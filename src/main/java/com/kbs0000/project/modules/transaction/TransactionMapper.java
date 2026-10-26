package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.modules.transaction.dto.TransactionRequestDto;
import com.kbs0000.project.modules.transaction.dto.TransactionResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public TransactionResponseDto toResponse(TransactionEntity entity) {

        if (entity == null) {
            return null;
        }

        return new TransactionResponseDto(
                entity.getId(),
                entity.getFinancialTransactionId(),
                entity.getTransactionType().name(),
                entity.getDatePosted().toString(),
                entity.getTransactionAmount().toString(),
                entity.getMemo()
        );
    }

    public List<TransactionResponseDto> toResponseList(List<TransactionEntity> entities) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionEntity toEntity(TransactionRequestDto request) {

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
