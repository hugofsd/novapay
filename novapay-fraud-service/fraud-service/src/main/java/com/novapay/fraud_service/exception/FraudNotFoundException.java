package com.novapay.fraud_service.exception;

public class FraudNotFoundException extends RuntimeException {

    public FraudNotFoundException(Long transactionId) {
        super("Análise de fraude não encontrada para transactionId: " + transactionId);
    }

}
