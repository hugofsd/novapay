package com.novapay.transaction_service.service;

import com.novapay.transaction_service.dto.request.TransactionRequest;
import com.novapay.transaction_service.dto.response.TransactionResponse;
import com.novapay.transaction_service.exception.TransactionNotFoundException;
import com.novapay.transaction_service.kafka.event.TransactionCreatedEvent;
import com.novapay.transaction_service.kafka.producer.TransactionEventProducer;
import com.novapay.transaction_service.model.Transaction;
import com.novapay.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer transactionEventProducer;

    public TransactionResponse create(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(request.getSourceAccountId());
        transaction.setTargetAccountId(request.getTargetAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created: id={}, type={}, amount={}", saved.getId(), saved.getType(), saved.getAmount());

        transactionEventProducer.publishTransactionCreated(TransactionCreatedEvent.builder()
                .id(saved.getId())
                .sourceAccountId(saved.getSourceAccountId())
                .targetAccountId(saved.getTargetAccountId())
                .amount(saved.getAmount())
                .type(saved.getType().name())
                .status(saved.getStatus().name())
                .createdAt(saved.getCreatedAt())
                .build());

        return toResponse(saved);
    }

    public List<TransactionResponse> findAll() {
        return transactionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return toResponse(transaction);
    }

    public List<TransactionResponse> findBySourceAccount(Long accountId) {
        return transactionRepository.findBySourceAccountId(accountId).stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .sourceAccountId(t.getSourceAccountId())
                .targetAccountId(t.getTargetAccountId())
                .amount(t.getAmount())
                .type(t.getType().name())
                .status(t.getStatus().name())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
