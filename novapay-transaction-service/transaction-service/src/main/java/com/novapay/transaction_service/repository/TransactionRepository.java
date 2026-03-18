package com.novapay.transaction_service.repository;

import com.novapay.transaction_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


// Spring Data JPA gera todas as implementações automaticamente.
// JpaRepository<Transaction, Long>:
//   - Transaction: entidade que este repository gerencia
//   - Long: tipo do ID
// Herda de graça: save(), findById(), findAll(), deleteById(), etc.
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    // Gera: SELECT * FROM transactions WHERE source_account_id = ?
    // Retorna todas as transações onde essa conta é a origem (quem enviou).
    List<Transaction> findBySourceAccountId(Long sourceAccountId);

    // Gera: SELECT * FROM transactions WHERE target_account_id = ?
    // Retorna todas as transações onde essa conta é o destino (quem recebeu).
    List<Transaction> findByTargetAccountId(Long targetAccountId);

}
