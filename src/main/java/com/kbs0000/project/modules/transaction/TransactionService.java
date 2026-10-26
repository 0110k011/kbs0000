package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.infra.shared.exception.DatabaseErrorException;
import com.kbs0000.project.modules.transaction.dto.TransactionRequestDto;
import com.kbs0000.project.modules.transaction.dto.TransactionResponseDto;
import com.kbs0000.project.modules.transaction.exception.OFXException;
import com.kbs0000.project.modules.transaction.exception.TransactionAlreadyExistsException;
import com.ofxreader.OFXReader;
import com.ofxreader.dto.AccountStatement;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
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

    public List<TransactionResponseDto> createTransactions(List<TransactionRequestDto> transactions) {

        List<String> financialTransactionIds = transactions.stream()
                .map(TransactionRequestDto::financialTransactionId)
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

    public AccountStatement mapOFXFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            OFXReader ofxReader = new OFXReader();
            return ofxReader.processOFX(inputStream);
        } catch (Exception e) {
            throw new OFXException(e.getMessage());
        }
    }
}
