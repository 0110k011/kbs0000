package com.kbs0000.project.modules.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private TransactionEntity createTestTransactionEntity(String financialTransactionId, TransactionType transactionType, BigDecimal transactionAmount, String memo) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setDatePosted(LocalDateTime.now());
        transactionEntity.setTransactionType(transactionType);
        transactionEntity.setFinancialTransactionId(financialTransactionId);
        transactionEntity.setTransactionAmount(transactionAmount);
        transactionEntity.setMemo(memo);
        return transactionEntity;
    }

    @Test
    void shouldFindTransactionByFinancialTransactionIdIn() {

        TransactionEntity transactionEntity0 = createTestTransactionEntity("UUID-000", TransactionType.DEBIT, new BigDecimal("100.00"), "Test memo 0");
        TransactionEntity transactionEntity1 = createTestTransactionEntity("UUID-001", TransactionType.CREDIT, new BigDecimal("222.00"), "Test memo 1");
        TransactionEntity transactionEntity2 = createTestTransactionEntity("UUID-002", TransactionType.DEBIT, new BigDecimal("999.99"), "Test memo 2");

        entityManager.persist(transactionEntity0);
        entityManager.persist(transactionEntity1);
        entityManager.persist(transactionEntity2);
        entityManager.flush();

        List<String> idsToSearch = List.of("UUID-000", "UUID-001");

        List<TransactionEntity> result = transactionRepository.findByFinancialTransactionIdIn(idsToSearch);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TransactionEntity::getFinancialTransactionId)
                .containsExactlyInAnyOrder("UUID-000", "UUID-001");

    }

    @Test
    void shouldReturnEmptyListWhenFinancialTransactionsIdsDoNotExist() {

        TransactionEntity transactionEntity0 = createTestTransactionEntity("UUID-000", TransactionType.DEBIT, new BigDecimal("100.00"), "Test memo 0");
        entityManager.persistAndFlush(transactionEntity0);

        List<String> idsToSearch = List.of("UUID-999", "UUID-888");

        List<TransactionEntity> result = transactionRepository.findByFinancialTransactionIdIn(idsToSearch);

        assertThat(result).isEmpty();
    }
}
