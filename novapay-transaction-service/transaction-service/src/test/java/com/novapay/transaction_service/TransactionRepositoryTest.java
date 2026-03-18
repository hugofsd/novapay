package com.novapay.transaction_service;


import com.novapay.transaction_service.model.Transaction;
import com.novapay.transaction_service.model.TransactionType;
import com.novapay.transaction_service.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest: sobe apenas a camada JPA com banco H2 em memória.
// Não sobe HTTP nem Kafka — testa só repository + entidade.
// Mais rápido e isolado do que @SpringBootTest.
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldSaveAndFindBySourceAccountId() {
        // Monta a transação — @PrePersist vai preencher status e createdAt
        Transaction t = new Transaction();
        t.setSourceAccountId(1L);
        t.setTargetAccountId(2L);
        t.setAmount(new BigDecimal("250.00"));
        t.setType(TransactionType.TRANSFER);

        transactionRepository.save(t); // dispara @PrePersist

        List<Transaction> found = transactionRepository.findBySourceAccountId(1L);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getAmount()).isEqualByComparingTo("250.00");
        assertThat(found.get(0).getStatus()).isNotNull(); // @PrePersist setou PENDING
        assertThat(found.get(0).getCreatedAt()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenAccountHasNoTransactions() {
        // Conta que nunca fez transação — deve retornar lista vazia
        List<Transaction> found = transactionRepository.findBySourceAccountId(999L);
        assertThat(found).isEmpty();
    }
}
