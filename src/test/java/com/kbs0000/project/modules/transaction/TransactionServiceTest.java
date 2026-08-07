package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.infra.shared.exception.DatabaseErrorException;
import com.kbs0000.project.modules.transaction.dto.TransactionRequest;
import com.kbs0000.project.modules.transaction.dto.TransactionResponse;
import com.kbs0000.project.modules.transaction.exception.TransactionAlreadyExistsException;
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

    private TransactionRequest createTestTransactionRequest(
            TransactionType transactionType,
            String amount,
            String memo) {
        return new TransactionRequest(
                transactionType,
                LocalDateTime.now(),
                new BigDecimal(amount),
                UUID.randomUUID().toString(),
                memo
        );
    }

    private TransactionResponse createTestTransactionResponse(TransactionEntity entity) {
        return new TransactionResponse(
                entity.getId(),
                entity.getFinancialTransactionId(),
                entity.getTransactionType().name(),
                entity.getDatePosted().toString(),
                entity.getTransactionAmount().toString(),
                entity.getMemo()
        );
    }

    private TransactionEntity createTestTransactionEntity(TransactionRequest request) {
        return TransactionEntity.builder()
                .id(UUID.randomUUID())
                .transactionType(request.transactionType())
                .datePosted(request.datePosted())
                .transactionAmount(request.transactionAmount())
                .financialTransactionId(request.financialTransactionId())
                .memo(request.memo())
                .build();
    }

    @Test
    void shouldSaveTransactionsSuccessfully() {

        TransactionRequest testRequest = createTestTransactionRequest(TransactionType.DEBIT, "100.00", "Test transaction");
        TransactionEntity testEntity = createTestTransactionEntity(testRequest);
        TransactionResponse testResponse = createTestTransactionResponse(testEntity);

        List<TransactionRequest> testRequests = List.of(testRequest);

        when(transactionRepository.findByFinancialTransactionIdIn(any())).thenReturn(Collections.emptyList());
        when(transactionMapper.toEntity(any(TransactionRequest.class))).thenReturn(testEntity);
        when(transactionRepository.saveAll(any())).thenReturn(List.of(testEntity));
        when(transactionMapper.toResponseList(any())).thenReturn(List.of(testResponse));

        List<TransactionResponse> responses = transactionService.createTransactions(testRequests);

        assertNotNull(responses);
        verify(transactionRepository, times(1)).findByFinancialTransactionIdIn(any());
        verify(transactionRepository, times(1)).saveAll(any());
    }

    @Test
    void shouldDeduplicatePayloadAndSaveUniques() {

        TransactionRequest testRequest = createTestTransactionRequest(TransactionType.DEBIT, "100.00", "Test transaction 0");
        TransactionEntity testEntity = createTestTransactionEntity(testRequest);
        TransactionResponse testResponse = createTestTransactionResponse(testEntity);

        List<TransactionRequest> testRequests = List.of(testRequest, testRequest);

        when(transactionRepository.findByFinancialTransactionIdIn(any())).thenReturn(Collections.emptyList());
        when(transactionMapper.toEntity(any(TransactionRequest.class))).thenReturn(testEntity);
        when(transactionRepository.saveAll(any())).thenReturn(List.of(testEntity));
        when(transactionMapper.toResponseList(any())).thenReturn(List.of(testResponse));

        List<TransactionResponse> responses = transactionService.createTransactions(testRequests);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(transactionRepository, times(1)).findByFinancialTransactionIdIn(any());
        verify(transactionMapper, times(1)).toEntity(any());
        verify(transactionRepository, times(1)).saveAll(any());
    }

    @Test
    void shouldFilterExistingTransactionsAndSaveOnlyNewOnes() {

        TransactionRequest testRequest0 = createTestTransactionRequest(TransactionType.DEBIT, "100.00", "Test transaction 0");
        TransactionRequest testRequest1 = createTestTransactionRequest(TransactionType.CREDIT, "999.00", "Test transaction 1");
        TransactionEntity testEntity0 = createTestTransactionEntity(testRequest0);
        TransactionEntity testEntity1 = createTestTransactionEntity(testRequest1);
        TransactionResponse testResponse1 = createTestTransactionResponse(testEntity1);

        List<TransactionRequest> testRequests = List.of(testRequest0, testRequest1);

        when(transactionRepository.findByFinancialTransactionIdIn(any())).thenReturn(List.of(testEntity0));
        when(transactionMapper.toEntity(testRequest1)).thenReturn(testEntity1);
        when(transactionRepository.saveAll(any())).thenReturn(List.of(testEntity1));
        when(transactionMapper.toResponseList(any())).thenReturn(List.of(testResponse1));

        List<TransactionResponse> responses = transactionService.createTransactions(testRequests);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(transactionRepository, times(1)).findByFinancialTransactionIdIn(any());
        verify(transactionMapper, times(1)).toEntity(any());
        verify(transactionRepository, times(1)).saveAll(any());
    }

    @Test
    void shouldThrowExceptionWhenAllTransactionsAlreadyExist() {

        TransactionRequest testRequest = createTestTransactionRequest(TransactionType.DEBIT, "100.00", "Test transaction");
        TransactionEntity testEntity = createTestTransactionEntity(testRequest);

        when(transactionRepository.findByFinancialTransactionIdIn(any())).thenReturn(List.of(testEntity));

        assertThrows(TransactionAlreadyExistsException.class, () -> {
            transactionService.createTransactions(List.of(testRequest));
        });

        verify(transactionRepository, never()).saveAll(any());
    }

    @Test
    void shouldThrowDatabaseErrorExceptionWhenSaveAllFails() {

        TransactionRequest testRequest = createTestTransactionRequest(TransactionType.DEBIT, "100.00", "Test transaction");
        TransactionEntity testEntity = createTestTransactionEntity(testRequest);

        when(transactionRepository.findByFinancialTransactionIdIn(any())).thenReturn(Collections.emptyList());
        when(transactionMapper.toEntity(any(TransactionRequest.class))).thenReturn(testEntity);
        when(transactionRepository.saveAll(any())).thenThrow(new RuntimeException("Database error"));

        DatabaseErrorException exception = assertThrows(DatabaseErrorException.class, () -> {
            transactionService.createTransactions(List.of(testRequest));
        });

        assertEquals("Error occurred while saving transaction(s)", exception.getMessage());

        verify(transactionRepository, times(1)).saveAll(any());
        verify(transactionMapper, never()).toResponseList(any());
    }
}
