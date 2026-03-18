package com.novapay.transaction_service.model;

// Ciclo de vida de uma transação:
// PENDING → COMPLETED (caminho feliz)
// PENDING → FAILED    (fraude detectada, conta bloqueada, etc.)
// Uma transação NUNCA volta de COMPLETED para PENDING.
public enum TransactionStatus {
    PENDING,    // recém registrada, aguardando validação do fraud-service
    COMPLETED,  // aprovada e processada
    FAILED      // rejeitada por algum motivo
}
