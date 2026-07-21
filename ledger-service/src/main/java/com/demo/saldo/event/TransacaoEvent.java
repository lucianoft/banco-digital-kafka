package com.demo.saldo.event;

import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;

import java.math.BigDecimal;

public record TransacaoEvent(String correlationId, Long contaId, BigDecimal valor, TipoMovimento tipoMovimento,
                              TipoTransacao tipoTransacao) {
}
