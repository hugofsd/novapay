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

// Toda a lógica de negócio fica aqui.
// O controller só delega — não tem lógica. O repository só acessa o banco — não tem lógica.
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer transactionEventProducer;

    // Fluxo: recebe request → monta entidade → salva no banco → publica evento → retorna response
    public TransactionResponse create(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(request.getSourceAccountId());
        transaction.setTargetAccountId(request.getTargetAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        // status e createdAt são preenchidos pelo @PrePersist da entidade

        Transaction saved = transactionRepository.save(transaction); // dispara @PrePersist
        log.info("Transaction created: id={}, type={}, amount={}", saved.getId(), saved.getType(), saved.getAmount());

        // Publica o evento DEPOIS do banco confirmar o save.
        // .name() converte o enum para String: TransactionType.TRANSFER → "TRANSFER"
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

    // Retorna todas as transações — em produção usaria Pageable para não carregar tudo
    public List<TransactionResponse> findAll() {
        return transactionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // Busca por ID — lança TransactionNotFoundException se não existir → GlobalExceptionHandler retorna 404
    public TransactionResponse findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return toResponse(transaction);
    }

    // Retorna o histórico de transações onde a conta foi a origem (enviou dinheiro)
    public List<TransactionResponse> findBySourceAccount(Long accountId) {
        return transactionRepository.findBySourceAccountId(accountId).stream()
                .map(this::toResponse)
                .toList();
    }
    // Conversor privado: Transaction (entidade) → TransactionResponse (DTO de saída)
    // Centralizado aqui — se o DTO mudar, só muda neste método
    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .sourceAccountId(t.getSourceAccountId())
                .targetAccountId(t.getTargetAccountId())
                .amount(t.getAmount())
                .type(t.getType().name())       // enum → String
                .status(t.getStatus().name())   // enum → String
                .createdAt(t.getCreatedAt())
                .build();
    }


}
