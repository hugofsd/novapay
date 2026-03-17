package com.novapay.account_service.controller;


import com.novapay.account_service.dto.request.AccountRequest;
import com.novapay.account_service.dto.request.UpdateStatusRequest;
import com.novapay.account_service.dto.response.AccountResponse;
import com.novapay.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: combina @Controller + @ResponseBody.
//   Tudo que os métodos retornam é serializado como JSON automaticamente.
// @RequestMapping("/accounts"): prefixo de todos os endpoints desta classe.
// @RequiredArgsConstructor: injeta AccountService pelo construtor.
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    // O controller não conhece o banco — só conhece o service.
    // Isso permite trocar a implementação do service sem mudar o controller.
    private final AccountService accountService;

    // POST /accounts — cria uma nova conta
    // @Valid: ativa as validações do AccountRequest (@NotBlank, @Pattern).
    //         Se falhar, o GlobalExceptionHandler retorna 400 com os erros.
    // ResponseEntity.status(CREATED): retorna HTTP 201 em vez do 200 padrão.
    //   201 = "Created" — semanticamente correto para POST que cria recurso.
    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
    }

    // GET /accounts — lista todas as contas
    // ResponseEntity.ok() = HTTP 200 OK (padrão para GET bem-sucedido).
    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    // GET /accounts/{id} — busca uma conta pelo ID
    // @PathVariable: extrai o {id} da URL e passa como parâmetro Long.
    // Se não encontrar, o service lança AccountNotFoundException → 404.
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    // PATCH /accounts/{id}/status — atualiza apenas o status da conta
    // PATCH (em vez de PUT) porque atualizamos parcialmente o recurso.
    // PUT substituiria o recurso inteiro.
    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(accountService.updateStatus(id, request.getStatus()));
    }


}
