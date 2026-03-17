package com.novapay.account_service.repository;

import com.novapay.account_service.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// jpa gera as querys automaticamente
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Spring Data interpreta o nome do método e gera o SQL:
    // "findBy" + "Cpf" → SELECT * FROM accounts WHERE cpf = ?
    // Optional<> evita NullPointerException: o chamador é forçado a tratar o "não encontrado".
    Optional<Account> findByCpf(String cpf);

    // Gera: SELECT COUNT(*) > 0 FROM accounts WHERE cpf = ?
    // Útil para validar duplicata antes de salvar (retorna true/false).
    boolean existsByCpf(String cpf);

}
