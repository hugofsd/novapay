package com.novapay.account_service.service;

import com.novapay.account_service.dto.request.AccountRequest;
import com.novapay.account_service.dto.response.AccountResponse;
import com.novapay.account_service.exception.AccountNotFoundException;
import com.novapay.account_service.kafka.event.AccountCreatedEvent;
import com.novapay.account_service.kafka.producer.AccountEventProducer;
import com.novapay.account_service.model.Account;
import com.novapay.account_service.model.AccountStatus;
import com.novapay.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


// @Service: indica que é um bean de negócio. O Spring o registra e injeta onde necessário.
// Toda a lógica de negócio fica aqui — o controller só delega, o repository só acessa o banco.
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountEventProducer accountEventProducer;

    // Cria uma nova conta a partir dos dados do request.
    // Fluxo: valida (feito pelo @Valid no controller) → salva no banco → publica evento.
    public AccountResponse create(AccountRequest request) {

        Account account = new Account();
        account.setOwnerName(request.getOwnerName());
        account.setCpf(request.getCpf());
        // balance e status NÃO são setados aqui — o @PrePersist da entidade cuida disso


        Account saved = accountRepository.save(account); // dispara o @PrePersist aqui
        log.info("Account created: id={}, cpf={}", saved.getId(), saved.getCpf());

        // Publica o evento APÓS salvar com sucesso.
        // .name() converte o enum AccountStatus para a String "ACTIVE".
        accountEventProducer.publishAccountCreated(AccountCreatedEvent.builder()
                .id(saved.getId())
                .ownerName(saved.getOwnerName())
                .cpf(saved.getCpf())
                .balance(saved.getBalance())
                .status(saved.getStatus().name())
                .createdAt(saved.getCreatedAt())
                .build());

        return toResponse(saved);
    };

    // Retorna todas as contas. Em produção usaria paginação (Pageable),
    // mas para o escopo do projeto está adequado.
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(this::toResponse) // aplica toResponse() em cada Account da lista
                .toList();
    }

    // Busca por ID. orElseThrow lança AccountNotFoundException se não encontrar,
    // que o GlobalExceptionHandler converte em resposta 404.
    public AccountResponse findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return toResponse(account);
    }

    // Atualiza apenas o status. Busca a conta, seta o novo status e salva.
    // O @PreUpdate da entidade atualiza o updatedAt automaticamente.
    public AccountResponse updateStatus(Long id, AccountStatus status) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        account.setStatus(status);
        return toResponse(accountRepository.save(account)); // @PreUpdate é acionado aqui
    }

    // Método privado de conversão Account → AccountResponse (o "mapper" manual).
    // Centraliza a conversão em um lugar só — se AccountResponse mudar, muda só aqui.
    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .ownerName(account.getOwnerName())
                .cpf(account.getCpf())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

}
