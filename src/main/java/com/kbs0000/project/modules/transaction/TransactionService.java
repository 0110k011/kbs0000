package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.infra.shared.exception.DatabaseErrorException;
import com.kbs0000.project.modules.transaction.dto.TransactionRequest;
import com.kbs0000.project.modules.transaction.dto.TransactionResponse;
import com.kbs0000.project.modules.transaction.exception.TransactionAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public List<TransactionResponse> createTransactions(List<TransactionRequest> transactions) {

        List<String> financialTransactionIds = transactions.stream()
                .map(TransactionRequest::financialTransactionId)
                .toList();

        List<TransactionEntity> existingTransactions = transactionRepository.findByFinancialTransactionIdIn(financialTransactionIds);

        Set<String> existingTransactionSet = existingTransactions.stream()
                .map(TransactionEntity::getFinancialTransactionId)
                .collect(Collectors.toSet());

        Set<String> processedRequest = new HashSet<>();

        List<TransactionEntity> entities = transactions.stream()
                .filter(transaction -> !existingTransactionSet.contains(transaction.financialTransactionId()))
                .filter(transaction -> processedRequest.add(transaction.financialTransactionId()))
                .map(transactionMapper::toEntity)
                .toList();
        if (entities.isEmpty()) {
            throw new TransactionAlreadyExistsException();
        }

        try {
            List<TransactionEntity> savedEntities = transactionRepository.saveAll(entities);
            return transactionMapper.toResponseList(savedEntities);
        } catch (Exception e) {
            throw new DatabaseErrorException("Error occurred while saving transaction(s)", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
