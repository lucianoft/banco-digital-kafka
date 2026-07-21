package com.demo.pix.event;

import java.math.BigDecimal;

/** Espelha com.demo.saldo.event.TransacaoProcessadaEvent — é o que o ledger-service publica de volta. */
public record TransacaoProcessadaEvent(String correlationId, Long contaId, TipoMovimento tipoMovimento,
                                        TipoTransacao tipoTransacao, BigDecimal valor, boolean sucesso,
                                        String motivo) {
}
