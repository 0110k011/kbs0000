package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.modules.transaction.dto.BatchTransactionRequest;
import com.kbs0000.project.modules.transaction.dto.TransactionRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionEntity createTransaction(BatchTransactionRequest request) {

        List<String> financialTransactionIds = request.transactions().stream()
                .map(TransactionRequest::financialTransactionId)
                .toList();

        List<TransactionEntity> existingTransactions = transactionRepository.findByFinancialTransactionIdIn(financialTransactionIds);

        Set<String> existingTransactionSet = existingTransactions.stream()
                .map(TransactionEntity::getFinancialTransactionId)
                .collect(Collectors.toSet());

        Set<String> processedRequest = new HashSet<>();

        List<TransactionRequest> entities = request.transactions().stream()
                .filter(transaction -> !existingTransactionSet.contains(transaction.financialTransactionId()))
                .filter(transaction -> processedRequest.add(transaction.financialTransactionId()))
                .toList();

        try {
            List<TransactionEntity> savedTransactions =
        }
        return transactionRepository.save(request);
    }
}
