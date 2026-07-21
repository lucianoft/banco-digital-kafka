package com.demo.pix.mapper;

import com.demo.pix.dto.PixTransacaoResponse;
import com.demo.pix.entity.PixTransacao;
import com.demo.pix.entity.StatusPix;
import com.demo.pix.event.TipoMovimento;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PixTransacaoMapperTest {

    private final PixTransacaoMapper mapper = Mappers.getMapper(PixTransacaoMapper.class);

    @Test
    void toResponse_deveMapearTodosOsCampos() {
        PixTransacao pix = new PixTransacao();
        pix.setTransacaoId(UUID.randomUUID());
        pix.setContaId(1L);
        pix.setValor(new BigDecimal("50.00"));
        pix.setTipoMovimento(TipoMovimento.DEBITO);
        pix.setStatus(StatusPix.CONFIRMADA);
        pix.setMotivo("ok");
        pix.setCriadoEm(LocalDateTime.of(2026, 1, 1, 10, 0));
        pix.setAtualizadoEm(LocalDateTime.of(2026, 1, 1, 10, 1));

        PixTransacaoResponse response = mapper.toResponse(pix);

        assertThat(response.transacaoId()).isEqualTo(pix.getTransacaoId());
        assertThat(response.contaId()).isEqualTo(1L);
        assertThat(response.valor()).isEqualByComparingTo("50.00");
        assertThat(response.tipoMovimento()).isEqualTo(TipoMovimento.DEBITO);
        assertThat(response.status()).isEqualTo(StatusPix.CONFIRMADA);
        assertThat(response.motivo()).isEqualTo("ok");
    }
}
