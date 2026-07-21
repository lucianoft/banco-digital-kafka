package com.demo.pix.event;

import java.math.BigDecimal;

/** Espelha com.demo.saldo.event.TransacaoEvent — é o que o pix-service publica no tópico transacoes. */
public record TransacaoEvent(String correlationId, Long contaId, BigDecimal valor, TipoMovimento tipoMovimento,
                              TipoTransacao tipoTransacao) {
}
