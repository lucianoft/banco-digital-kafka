package com.demo.saldo.exception;

/** Erro de regra de negócio (conta inexistente/inativa) — não deve ser reprocessado, vai direto pra DLQ. */
public class ContaInvalidaException extends RuntimeException {

    public ContaInvalidaException(String message) {
        super(message);
    }
}
