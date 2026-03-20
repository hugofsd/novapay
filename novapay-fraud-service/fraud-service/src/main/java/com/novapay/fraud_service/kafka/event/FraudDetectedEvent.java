package com.novapay.fraud_service.kafka.event;

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
public class FraudDetectedEvent {

    private Long transactionId;
    private BigDecimal amount;

    // String em vez de enum: desacopla o consumer do enum interno do fraud-service
    private String status;   // "APPROVED" ou "SUSPICIOUS"
    private String reason;   // null quando APPROVED

    private LocalDateTime analyzedAt;




}
