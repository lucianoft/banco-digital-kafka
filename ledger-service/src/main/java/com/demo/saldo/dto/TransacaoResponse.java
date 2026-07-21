package com.demo.saldo.dto;

import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Se existe resposta pra esse correlationId, a transação foi efetivada — não existe mais estado intermediário. */
public record TransacaoResponse(String correlationId, BigDecimal valor, TipoMovimento tipoMovimento,
                                 TipoTransacao tipoTransacao, LocalDateTime criadoEm) implements Serializable {
}
