package com.kbs0000.project.modules.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbs0000.project.modules.transaction.dto.TransactionRequestDto;
import com.kbs0000.project.modules.transaction.dto.TransactionResponseDto;
import com.kbs0000.project.modules.transaction.exception.TransactionAlreadyExistsException;
import com.ofxreader.dto.AccountStatement;
import com.ofxreader.dto.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private TransactionRequestDto cretateValidTransactionRequest() {
        return new TransactionRequestDto(
                TransactionType.DEBIT,
                LocalDateTime.now(),
                new BigDecimal("100.00"),
                UUID.randomUUID().toString(),
                "Test transaction"
        );
    }

    private TransactionResponseDto createValidTransactionResponse() {
        return new TransactionResponseDto(
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                TransactionType.DEBIT.name(),
                LocalDateTime.now().toString(),
                new BigDecimal("100.00").toString(),
                "Test transaction"
        );
    }

    @Test
    void shouldReturn201CreatedWhenTransactionsAreCreated() throws Exception {

        TransactionRequestDto testRequest = cretateValidTransactionRequest();
        TransactionResponseDto testResponse = createValidTransactionResponse();

        when(transactionService.createTransactions(anyList())).thenReturn(List.of(testResponse));

        mockMvc.perform(post("/api/v1/transactions/{accountId}", "testAccountId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of(testRequest))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].financialTransactionId").value(testResponse.financialTransactionId()))
                .andExpect(jsonPath("$[0].transactionAmount").value("100.00"));
    }

    @Test
    void shouldReturn400BadRequestWhenListIsEmpty() throws Exception {

        mockMvc.perform(post("/api/v1/transactions/{accountId}", "testAccountId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed with 1 error(s)")))
                .andExpect(jsonPath("$.invalidFields[0].field").value("requests"))
                .andExpect(jsonPath("$.invalidFields[0].message").value("Transaction request list cannot be empty"));
    }

    @Test
    void shouldReturn400BadRequestWhenDtoIsInvalid() throws Exception {

        TransactionRequestDto invalidRequest = new TransactionRequestDto(
                TransactionType.DEBIT,
                LocalDateTime.now(),
                new BigDecimal("100.00"),
                "", // Invalid financialTransactionId
                "Store Purchase Test"
        );

        mockMvc.perform(post("/api/v1/transactions/{accountId}", "testAccountId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of(invalidRequest))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed with 1 error(s)")))
                .andExpect(jsonPath("$.invalidFields", hasSize(1)))
                .andExpect(jsonPath("$.invalidFields[*].field", containsInAnyOrder("financialTransactionId")));
    }

    @Test
    void shouldReturn409ConflictWhenAllTransactionsAlreadyExist() throws Exception {

        TransactionRequestDto testRequest = cretateValidTransactionRequest();

        when(transactionService.createTransactions(anyList()))
                .thenThrow(new TransactionAlreadyExistsException());

        mockMvc.perform(post("/api/v1/transactions/{accountId}", "testAccountId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of(testRequest))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Financial Transaction(s) already exists")));
    }

    @Test
    void shouldUploadOfxFileSuccessfully() throws Exception {
        String ofxContent = "OFXHEADER:100\nDATA:OFXSGML\n<OFX></OFX>";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.ofx",
                "application/octet-stream",
                ofxContent.getBytes()
        );

        Transaction testTransaction1 = new Transaction(
                "20230115001",
                new BigDecimal("-50.00"),
                LocalDateTime.parse("2023-01-15T00:00:00"),
                "Store Purchase 1",
                "DEBIT"
        );

        AccountStatement mockStatement = new AccountStatement(
                "BANK S.A.",
                "BRL",
                "0000",
                "1",
                "00000000-0",
                "CHECKING",
                LocalDateTime.parse("2025-01-01T00:00:00"),
                LocalDateTime.parse("2025-01-31T00:00:00"),
                List.of(testTransaction1),
                0
        );
        when(transactionService.mapOFXFile(any())).thenReturn(mockStatement);

        mockMvc.perform(multipart("/api/v1/transactions")
                    .file(file))
                .andExpect(status().isOk());
    }
}
