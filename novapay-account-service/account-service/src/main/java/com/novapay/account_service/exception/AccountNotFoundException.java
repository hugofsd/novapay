package com.novapay.account_service.exception;


// RuntimeException: não precisa de try/catch — o GlobalExceptionHandler captura.
// Ter uma exception específica (em vez de lançar RuntimeException genérica)
// permite tratar cada tipo de erro de forma diferente no handler.
public class AccountNotFoundException extends  RuntimeException{

    public AccountNotFoundException (Long id){
        super("Account not found with id: " + id);    }
}
