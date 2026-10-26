package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.modules.transaction.dto.TransactionRequestDto;
import com.kbs0000.project.modules.transaction.dto.TransactionResponseDto;
import com.ofxreader.dto.AccountStatement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("{accountId}")
    public ResponseEntity<List<TransactionResponseDto>> createTransactions(
            @PathVariable("accountId") String accountId,
            @RequestBody
            @NotEmpty(message = "Transaction request list cannot be empty")
            @Valid List<TransactionRequestDto> requests) {
        List<TransactionResponseDto> transactions = transactionService.createTransactions(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactions);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AccountStatement> mapOFXFile(
            @RequestParam("file") MultipartFile file
    ) {
        AccountStatement statement = transactionService.mapOFXFile(file);
        return ResponseEntity.ok(statement);
    }
}
