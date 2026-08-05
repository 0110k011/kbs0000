package com.kbs0000.project.modules.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends
        JpaRepository<TransactionEntity, UUID>,
        JpaSpecificationExecutor<TransactionEntity> {

    List<TransactionEntity> findByFinancialTransactionIdIn(List<String> financialTransactionIds);
}
