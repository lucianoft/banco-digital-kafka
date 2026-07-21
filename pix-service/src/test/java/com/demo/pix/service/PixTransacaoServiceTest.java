package com.demo.pix.service;

import com.demo.pix.entity.PixTransacao;
import com.demo.pix.entity.StatusPix;
import com.demo.pix.event.TipoMovimento;
import com.demo.pix.repository.PixTransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PixTransacaoServiceTest {

    @Mock
    private PixTransacaoRepository pixTransacaoRepository;

    private PixTransacaoService pixTransacaoService;

    @BeforeEach
    void setUp() {
        pixTransacaoService = new PixTransacaoService(pixTransacaoRepository);
    }

    @Test
    void registrar_deveCriarComStatusEnviada() {
        UUID transacaoId = UUID.randomUUID();
        when(pixTransacaoRepository.save(any(PixTransacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PixTransacao resultado = pixTransacaoService.registrar(transacaoId, 1L, new BigDecimal("10.00"), TipoMovimento.DEBITO);

        assertThat(resultado.getTransacaoId()).isEqualTo(transacaoId);
        assertThat(resultado.getContaId()).isEqualTo(1L);
        assertThat(resultado.getValor()).isEqualByComparingTo("10.00");
        assertThat(resultado.getTipoMovimento()).isEqualTo(TipoMovimento.DEBITO);
        assertThat(resultado.getStatus()).isEqualTo(StatusPix.ENVIADA);
        assertThat(resultado.getCriadoEm()).isNotNull();
        assertThat(resultado.getAtualizadoEm()).isNotNull();
    }

    @Test
    void finalizar_deveMarcarConfirmada_quandoSucesso() {
        UUID transacaoId = UUID.randomUUID();
        PixTransacao existente = new PixTransacao();
        existente.setTransacaoId(transacaoId);
        existente.setStatus(StatusPix.ENVIADA);
        when(pixTransacaoRepository.findByTransacaoId(transacaoId)).thenReturn(Optional.of(existente));

        Optional<PixTransacao> resultado = pixTransacaoService.finalizar(transacaoId, true, "Processado com sucesso");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getStatus()).isEqualTo(StatusPix.CONFIRMADA);
        assertThat(resultado.get().getMotivo()).isEqualTo("Processado com sucesso");
    }

    @Test
    void finalizar_deveMarcarRejeitada_quandoFalha() {
        UUID transacaoId = UUID.randomUUID();
        PixTransacao existente = new PixTransacao();
        existente.setStatus(StatusPix.ENVIADA);
        when(pixTransacaoRepository.findByTransacaoId(transacaoId)).thenReturn(Optional.of(existente));

        Optional<PixTransacao> resultado = pixTransacaoService.finalizar(transacaoId, false, "Saldo insuficiente");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getStatus()).isEqualTo(StatusPix.REJEITADA);
    }

    @Test
    void finalizar_deveRetornarVazio_quandoTransacaoIdDesconhecido() {
        UUID transacaoId = UUID.randomUUID();
        when(pixTransacaoRepository.findByTransacaoId(transacaoId)).thenReturn(Optional.empty());

        Optional<PixTransacao> resultado = pixTransacaoService.finalizar(transacaoId, true, "motivo");

        assertThat(resultado).isEmpty();
    }

    @Test
    void buscarPorTransacaoId_deveDelegarParaRepository() {
        UUID transacaoId = UUID.randomUUID();
        PixTransacao pix = new PixTransacao();
        when(pixTransacaoRepository.findByTransacaoId(transacaoId)).thenReturn(Optional.of(pix));

        assertThat(pixTransacaoService.buscarPorTransacaoId(transacaoId)).contains(pix);
    }
}
