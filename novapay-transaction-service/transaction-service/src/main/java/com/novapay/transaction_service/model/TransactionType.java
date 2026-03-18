package com.novapay.transaction_service.model;


// Define os tipos possíveis de movimentação financeira.
// Usar enum garante que só esses 3 valores existam no banco — nunca uma String aleatória.
public enum TransactionType {
    TRANSFER,    // transferência de uma conta para outra
    DEPOSIT,     // entrada de dinheiro na conta (ex: recarga)
    WITHDRAWAL   // saída de dinheiro da conta (ex: saque)
}