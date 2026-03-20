package com.novapay.fraud_service.kafka.event;

import lombok.*;

import java.math.BigDecimal;

// Espelho do evento publicado pelo transaction-service
// Contém apenas os campos necessários para as regras de fraude
// Campos extras no JSON (ex: createdAt) são ignorados automaticamente pelo Jackson
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreatedEvent {

    private Long id;
    private Long sourceAccountId;  // regra: Same account
    private Long targetAccountId;  // regra: Same account
    private BigDecimal amount;     // regra: High amount
    private String type;
    private String status;
    // createdAt removido: fraud-service não usa este campo nas regras
    // e LocalDateTime exigiria módulo Jackson não disponível no Spring Boot 4.x

}
