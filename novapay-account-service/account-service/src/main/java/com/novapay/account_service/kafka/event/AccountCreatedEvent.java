package com.novapay.account_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent {

    private Long id;
    private String ownerName;
    private String cpf;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
}
