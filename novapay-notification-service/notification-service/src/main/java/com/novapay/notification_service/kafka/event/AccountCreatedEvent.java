package com.novapay.notification_service.kafka.event;

import lombok.*;

import java.math.BigDecimal;

// SEM LocalDateTime — Spring Boot 4.0.4 não tem jsr310 disponível para o Kafka consumer
// Jackson ignora o campo createdAt do JSON automaticamente (FAIL_ON_UNKNOWN_PROPERTIES=false)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreatedEvent {

    private Long id;
    private String ownerName;
    private String cpf;
    private BigDecimal balance;
    private String status;
    // createdAt omitido intencionalmente
}
