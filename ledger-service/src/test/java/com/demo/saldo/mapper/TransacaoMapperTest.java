package com.demo.saldo.mapper;

import com.demo.saldo.dto.TransacaoResponse;
import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;
import com.demo.saldo.entity.Transacao;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransacaoMapperTest {

    private final TransacaoMapper mapper = Mappers.getMapper(TransacaoMapper.class);

    @Test
    void toResponse_deveMapearTodosOsCampos() {
        Transacao transacao = new Transacao();
        transacao.setCorrelationId("c1");
        transacao.setValor(new BigDecimal("10.00"));
        transacao.setTipoMovimento(TipoMovimento.CREDITO);
        transacao.setTipoTransacao(TipoTransacao.DINHEIRO);
        transacao.setCriadoEm(LocalDateTime.of(2026, 1, 1, 10, 0));

        TransacaoResponse response = mapper.toResponse(transacao);

        assertThat(response.correlationId()).isEqualTo("c1");
        assertThat(response.valor()).isEqualByComparingTo("10.00");
        assertThat(response.tipoMovimento()).isEqualTo(TipoMovimento.CREDITO);
        assertThat(response.tipoTransacao()).isEqualTo(TipoTransacao.DINHEIRO);
        assertThat(response.criadoEm()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0));
    }
}
