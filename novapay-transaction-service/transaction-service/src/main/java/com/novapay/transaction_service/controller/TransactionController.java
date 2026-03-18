package com.novapay.transaction_service.controller;

import com.novapay.transaction_service.dto.request.TransactionRequest;
import com.novapay.transaction_service.dto.response.TransactionResponse;
import com.novapay.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: todos os retornos são serializados como JSON automaticamente.
// @RequestMapping("/transactions"): prefixo de todos os endpoints desta classe.
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // POST /transactions — registra uma nova transação
    // @Valid: ativa as validações do TransactionRequest (@NotNull, @Positive, etc.)
    // 201 Created: semanticamente correto para POST que cria recurso
    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(request));
    }

    // GET /transactions — lista todas as transações
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    // GET /transactions/{id} — busca uma transação pelo ID
    // Se não existir → service lança TransactionNotFoundException → GlobalExceptionHandler → 404
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    // GET /transactions/account/{accountId} — histórico de uma conta
    // Retorna todas as transações onde essa conta foi a origem (enviou dinheiro)
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> findByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.findBySourceAccount(accountId));
    }
}
