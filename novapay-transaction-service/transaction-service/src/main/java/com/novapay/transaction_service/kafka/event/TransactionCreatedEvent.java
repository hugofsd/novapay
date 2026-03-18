package com.novapay.transaction_service.kafka.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Envelope da mensagem publicada no tópico "transaction-events".
// O fraud-service vai consumir este evento e analisar se a transação é suspeita.
// O notification-service vai consumir e notificar o usuário da movimentação.
// Contém uma cópia dos dados relevantes — não a entidade Transaction inteira.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreatedEvent {

    private Long id;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private String type;        // String porque quem consome não depende do enum deste serviço
    private String status;
    private LocalDateTime createdAt;

}
