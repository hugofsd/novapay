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

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountEventProducer accountEventProducer;

    public AccountResponse create(AccountRequest request) {
        Account account = new Account();
        account.setOwnerName(request.getOwnerName());
        account.setCpf(request.getCpf());

        Account saved = accountRepository.save(account);
        log.info("Account created: id={}, cpf={}", saved.getId(), saved.getCpf());

        accountEventProducer.publishAccountCreated(AccountCreatedEvent.builder()
                .id(saved.getId())
                .ownerName(saved.getOwnerName())
                .cpf(saved.getCpf())
                .balance(saved.getBalance())
                .status(saved.getStatus().name())
                .createdAt(saved.getCreatedAt())
                .build());

        return toResponse(saved);
    }

    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return toResponse(account);
    }

    public AccountResponse updateStatus(Long id, AccountStatus status) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        account.setStatus(status);
        return toResponse(accountRepository.save(account));
    }

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
