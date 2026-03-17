package com.novapay.account_service;

import com.novapay.account_service.model.Account;
import com.novapay.account_service.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


// @DataJpaTest: sobe apenas a camada JPA (banco em memória H2).
// Não sobe o servidor HTTP nem o Kafka — só testa repository + entidade.
// É mais rápido que @SpringBootTest.
    @DataJpaTest
    class AccountRepositoryTest {

        @Autowired
        private AccountRepository accountRepository;

        @Test
        void shouldSaveAndFindByCpf() {
            // Monta a entidade manualmente (o @PrePersist vai preencher balance, status e datas)
            Account account = new Account();
            account.setOwnerName("João Silva");
            account.setCpf("12345678901");

            // Salva — dispara o @PrePersist
            accountRepository.save(account);

            // Busca pelo CPF e valida que voltou com os dados corretos
            Optional<Account> found = accountRepository.findByCpf("12345678901");

            assertThat(found).isPresent();
            assertThat(found.get().getOwnerName()).isEqualTo("João Silva");
            assertThat(found.get().getBalance()).isNotNull();  // @PrePersist setou ZERO
            assertThat(found.get().getStatus()).isNotNull();   // @PrePersist setou ACTIVE
        }

        @Test
        void shouldReturnEmptyWhenCpfNotFound() {
            // CPF que não existe no banco — deve retornar Optional vazio
            Optional<Account> found = accountRepository.findByCpf("99999999999");
            assertThat(found).isEmpty();
        }
}
