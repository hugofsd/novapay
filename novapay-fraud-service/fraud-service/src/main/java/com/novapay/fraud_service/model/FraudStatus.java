package com.novapay.fraud_service.model;


public enum FraudStatus {
    APPROVED,   // Passou em todas as regras — pode prosseguir
    SUSPICIOUS  // Acionou pelo menos uma regra de risco — requer revisão
}
