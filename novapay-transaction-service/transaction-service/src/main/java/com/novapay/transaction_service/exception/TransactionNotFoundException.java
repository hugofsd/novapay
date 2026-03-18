package com.novapay.transaction_service.exception;

// RuntimeException: não exige try/catch — o GlobalExceptionHandler captura.
// Mensagem específica ajuda o cliente da API a entender exatamente o que não foi encontrado.
public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(Long id) {
        super("Transaction not found with id: " + id);
    }
}