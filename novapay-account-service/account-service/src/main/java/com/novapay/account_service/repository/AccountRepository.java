package com.novapay.account_service.repository;

import com.novapay.account_service.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
