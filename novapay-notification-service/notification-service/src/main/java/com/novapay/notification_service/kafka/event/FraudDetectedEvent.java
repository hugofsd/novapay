package com.novapay.notification_service.kafka.event;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudDetectedEvent {
    private Long transactionId;
    private BigDecimal amount;
    private String status;   // "APPROVED" ou "SUSPICIOUS"
    private String reason;   // null quando APPROVED
}
