package com.novapay.fraud_service.dto.response;

import com.novapay.fraud_service.model.FraudAnalysis;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO: o que o cliente vê no GET /fraud/{transactionId}
// Nunca expõe a entidade JPA diretamente (campos internos ficam encapsulados)
@Getter
@Builder
public class FraudAnalysisResponse {

    private Long transactionId;
    private BigDecimal amount;
    private String status;  // "APPROVED" ou "SUSPICIOUS"
    private String reason;  // null quando APPROVED
    private LocalDateTime analyzedAt;

    // Factory method: converte entidade → DTO num único lugar
    public static FraudAnalysisResponse from(FraudAnalysis analysis) {
        return FraudAnalysisResponse.builder()
                .transactionId(analysis.getTransactionId())
                .amount(analysis.getAmount())
                .status(analysis.getStatus().name())
                .reason(analysis.getReason())
                .analyzedAt(analysis.getAnalyzedAt())
                .build();
    }

}
