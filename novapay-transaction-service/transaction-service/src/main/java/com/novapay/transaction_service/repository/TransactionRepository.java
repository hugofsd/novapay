package com.novapay.transaction_service.repository;

import com.novapay.transaction_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySourceAccountId(Long sourceAccountId);

    List<Transaction> findByTargetAccountId(Long targetAccountId);
}
