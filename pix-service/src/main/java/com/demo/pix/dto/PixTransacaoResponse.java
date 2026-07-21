package com.demo.pix.dto;

import com.demo.pix.entity.StatusPix;
import com.demo.pix.event.TipoMovimento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PixTransacaoResponse(UUID transacaoId, Long contaId, BigDecimal valor, TipoMovimento tipoMovimento,
                                    StatusPix status, String motivo, LocalDateTime criadoEm,
                                    LocalDateTime atualizadoEm) implements Serializable {
}
