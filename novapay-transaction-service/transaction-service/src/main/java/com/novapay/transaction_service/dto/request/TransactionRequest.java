package com.novapay.transaction_service.dto.request;

import com.novapay.transaction_service.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// O que o cliente envia no corpo do POST /transactions.
// Contém apenas o que é necessário — o service preenche o resto (status, createdAt).
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    // @NotNull: não aceita null (diferente de @NotBlank que é só para String)
    @NotNull(message = "Source account is required")
    private Long sourceAccountId;

    @NotNull(message = "Target account is required")
    private Long targetAccountId;

    // @Positive: rejeita zero e valores negativos — uma transação precisa ter valor real
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    // Se o JSON enviar um tipo inválido (ex: "PIX"), o Jackson retorna 400
    // antes mesmo de chegar na validação @NotNull.
    @NotNull(message = "Type is required")
    private TransactionType type;




}
