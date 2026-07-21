package com.demo.pix.dto;

import com.demo.pix.event.TipoMovimento;

import java.math.BigDecimal;

/** Corpo do POST /pix — o tipo de transação é implicitamente PIX e o id de correlação é gerado pelo pix-service, não pelo chamador. */
public record PixMovimentacaoRequest(Long contaId, BigDecimal valor, TipoMovimento tipoMovimento) {
}
