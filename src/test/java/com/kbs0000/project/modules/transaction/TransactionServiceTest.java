package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.modules.transaction.dto.TransactionRequest;
import com.kbs0000.project.modules.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Test for processing a batch of transactions")
    void shouldSaveTransactionsSuccessfully() {

        TransactionRequest testRequest = new TransactionRequest(
                TransactionType.DEBIT,
                LocalDateTime.now(),
                new BigDecimal("100.00"),
                "00000000-aaaa-0000-aaaa-000000000000",
                "Store Purchase Test"
        );

        TransactionEntity testEntity = new TransactionEntity(
                UUID.randomUUID(),
                testRequest.transactionType(),
                testRequest.datePosted(),
                testRequest.transactionAmount(),
                testRequest.memo(),
                testRequest.financialTransactionId()
        );

        TransactionResponse testResponse = new TransactionResponse(
                testEntity.getId(),
                testEntity.getFinancialTransactionId(),
                testEntity.getTransactionType().name(),
                testEntity.getDatePosted().toString(),
                testEntity.getTransactionAmount().toString(),
                testEntity.getMemo()
        );

        List<TransactionRequest> testRequests = List.of(testRequest);

        when(transactionRepository.findByFinancialTransactionIdIn(any())).thenReturn(Collections.emptyList());
        when(transactionRepository.saveAll(any())).thenReturn(List.of(testEntity));
        when(transactionMapper.toEntity(any(TransactionRequest.class))).thenReturn(testEntity);
        when(transactionMapper.toResponseList(any())).thenReturn(List.of(testResponse));

        List<TransactionResponse> responses = transactionService.createTransactions(testRequests);

        assertNotNull(responses);
        verify(transactionRepository, times(1)).findByFinancialTransactionIdIn(any());
        verify(transactionRepository, times(1)).saveAll(any());
    }
}
