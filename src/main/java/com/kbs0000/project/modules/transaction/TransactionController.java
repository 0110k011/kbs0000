package com.kbs0000.project.modules.transaction;

import com.kbs0000.project.modules.transaction.dto.TransactionRequest;
import com.kbs0000.project.modules.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<TransactionResponse>> createTransactions(
            @PathVariable("accountId") String accountId,
            @RequestBody
            @NotEmpty(message = "Transaction request list cannot be empty")
            @Valid List<TransactionRequest> requests) {
        List<TransactionResponse> transactions = transactionService.createTransactions(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactions);
    }

}
