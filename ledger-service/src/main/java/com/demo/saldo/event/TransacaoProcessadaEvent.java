package com.demo.saldo.event;

import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;

import java.math.BigDecimal;

/** Publicado pelo ledger-service em transacoes-processadas pra toda tentativa, com sucesso ou erro. */
public record TransacaoProcessadaEvent(String correlationId, Long contaId, TipoMovimento tipoMovimento,
                                        TipoTransacao tipoTransacao, BigDecimal valor, boolean sucesso,
                                        String motivo) {
}
