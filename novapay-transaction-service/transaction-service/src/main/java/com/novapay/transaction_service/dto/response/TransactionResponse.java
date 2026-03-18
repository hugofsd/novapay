package com.novapay.transaction_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// O que a API devolve — nunca devolva a entidade Transaction diretamente.
// status e type são String (não enum) para que o consumidor da API
// receba texto legível: "PENDING", "TRANSFER", etc.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private String type;       // String, não enum
    private String status;     // String, não enum
    private LocalDateTime createdAt;

}
