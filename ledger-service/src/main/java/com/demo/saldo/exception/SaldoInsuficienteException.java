package com.demo.saldo.exception;

/** Erro de regra de negócio (débito maior que o saldo disponível) — não deve ser reprocessado, vai direto pra DLQ. */
public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
