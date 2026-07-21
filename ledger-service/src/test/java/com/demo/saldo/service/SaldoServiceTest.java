package com.demo.saldo.service;

import com.demo.saldo.client.ContaClient;
import com.demo.saldo.dto.ContaResumo;
import com.demo.saldo.entity.Saldo;
import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;
import com.demo.saldo.entity.Transacao;
import com.demo.saldo.event.TransacaoEvent;
import com.demo.saldo.exception.ContaInvalidaException;
import com.demo.saldo.exception.SaldoInsuficienteException;
import com.demo.saldo.repository.SaldoRepository;
import com.demo.saldo.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaldoServiceTest {

    @Mock
    private SaldoRepository saldoRepository;
    @Mock
    private TransacaoRepository transacaoRepository;
    @Mock
    private ContaClient contaClient;
    @Mock
    private SaldoDiarioService saldoDiarioService;

    @InjectMocks
    private SaldoService saldoService;

    private static Saldo novoSaldo(Long contaId, String valor) {
        Saldo saldo = new Saldo();
        saldo.setContaId(contaId);
        saldo.setValor(new BigDecimal(valor));
        saldo.setAtualizadoEm(LocalDateTime.now());
        return saldo;
    }

    private static ContaResumo contaAtiva(Long contaId) {
        return new ContaResumo(contaId, 1L, "0001-1", "CORRENTE", "ATIVA");
    }

    @Test
    void processar_deveEfetivarCredito_quandoContaAtivaESaldoSuficiente() {
        TransacaoEvent evento = new TransacaoEvent("c1", 1L, new BigDecimal("50.00"),
                TipoMovimento.CREDITO, TipoTransacao.DINHEIRO);
        Saldo saldo = novoSaldo(1L, "100.00");

        when(transacaoRepository.existsByCorrelationId("c1")).thenReturn(false);
        when(contaClient.buscarConta(1L)).thenReturn(contaAtiva(1L));
        when(saldoRepository.findById(1L)).thenReturn(Optional.of(saldo));

        Optional<Transacao> resultado = saldoService.processar(evento);

        assertThat(resultado).isPresent();
        assertThat(saldo.getValor()).isEqualByComparingTo("150.00");
        verify(saldoDiarioService).registrar(eq(1L), any(), any(BigDecimal.class));

        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository).save(captor.capture());
        Transacao salva = captor.getValue();
        assertThat(salva.getContaId()).isEqualTo(1L);
        assertThat(salva.getCorrelationId()).isEqualTo("c1");
        assertThat(salva.getValor()).isEqualByComparingTo("50.00");
        assertThat(salva.getTipoMovimento()).isEqualTo(TipoMovimento.CREDITO);
        assertThat(salva.getTipoTransacao()).isEqualTo(TipoTransacao.DINHEIRO);
    }

    @Test
    void processar_deveEfetivarDebito_quandoSaldoSuficiente() {
        TransacaoEvent evento = new TransacaoEvent("c2", 1L, new BigDecimal("30.00"),
                TipoMovimento.DEBITO, TipoTransacao.PIX);
        Saldo saldo = novoSaldo(1L, "100.00");

        when(transacaoRepository.existsByCorrelationId("c2")).thenReturn(false);
        when(contaClient.buscarConta(1L)).thenReturn(contaAtiva(1L));
        when(saldoRepository.findById(1L)).thenReturn(Optional.of(saldo));

        Optional<Transacao> resultado = saldoService.processar(evento);

        assertThat(resultado).isPresent();
        assertThat(saldo.getValor()).isEqualByComparingTo("70.00");
    }

    @Test
    void processar_deveIgnorar_quandoCorrelationIdJaExiste() {
        TransacaoEvent evento = new TransacaoEvent("dup", 1L, BigDecimal.TEN,
                TipoMovimento.CREDITO, TipoTransacao.DINHEIRO);
        when(transacaoRepository.existsByCorrelationId("dup")).thenReturn(true);

        Optional<Transacao> resultado = saldoService.processar(evento);

        assertThat(resultado).isEmpty();
        verifyNoInteractions(contaClient, saldoDiarioService);
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void processar_deveLancarContaInvalida_quandoContaNaoAtiva() {
        TransacaoEvent evento = new TransacaoEvent("c3", 1L, BigDecimal.TEN,
                TipoMovimento.CREDITO, TipoTransacao.DINHEIRO);
        when(transacaoRepository.existsByCorrelationId("c3")).thenReturn(false);
        when(contaClient.buscarConta(1L)).thenReturn(new ContaResumo(1L, 1L, "0001-1", "CORRENTE", "INATIVA"));

        assertThatThrownBy(() -> saldoService.processar(evento))
                .isInstanceOf(ContaInvalidaException.class)
                .hasMessageContaining("não está ativa");

        verify(saldoRepository, never()).findById(any());
    }

    @Test
    void processar_deveLancarContaInvalida_quandoSaldoNaoEncontrado() {
        TransacaoEvent evento = new TransacaoEvent("c4", 1L, BigDecimal.TEN,
                TipoMovimento.CREDITO, TipoTransacao.DINHEIRO);
        when(transacaoRepository.existsByCorrelationId("c4")).thenReturn(false);
        when(contaClient.buscarConta(1L)).thenReturn(contaAtiva(1L));
        when(saldoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saldoService.processar(evento))
                .isInstanceOf(ContaInvalidaException.class)
                .hasMessageContaining("Saldo não encontrado");
    }

    @Test
    void processar_deveLancarSaldoInsuficiente_quandoDebitoMaiorQueSaldo() {
        TransacaoEvent evento = new TransacaoEvent("c5", 1L, new BigDecimal("500.00"),
                TipoMovimento.DEBITO, TipoTransacao.PIX);
        Saldo saldo = novoSaldo(1L, "100.00");

        when(transacaoRepository.existsByCorrelationId("c5")).thenReturn(false);
        when(contaClient.buscarConta(1L)).thenReturn(contaAtiva(1L));
        when(saldoRepository.findById(1L)).thenReturn(Optional.of(saldo));

        assertThatThrownBy(() -> saldoService.processar(evento))
                .isInstanceOf(SaldoInsuficienteException.class);

        verify(transacaoRepository, never()).save(any());
        verifyNoInteractions(saldoDiarioService);
    }
}
