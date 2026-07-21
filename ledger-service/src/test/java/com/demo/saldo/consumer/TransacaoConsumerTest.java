package com.demo.saldo.consumer;

import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;
import com.demo.saldo.entity.Transacao;
import com.demo.saldo.event.TransacaoEvent;
import com.demo.saldo.event.TransacaoProcessadaEvent;
import com.demo.saldo.exception.ContaInvalidaException;
import com.demo.saldo.exception.SaldoInsuficienteException;
import com.demo.saldo.service.SaldoService;
import com.demo.saldo.service.TransacaoProcessadaPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoConsumerTest {

    @Mock
    private SaldoService saldoService;
    @Mock
    private TransacaoProcessadaPublisher transacaoProcessadaPublisher;

    private TransacaoConsumer transacaoConsumer;

    @BeforeEach
    void setUp() {
        transacaoConsumer = new TransacaoConsumer(saldoService, transacaoProcessadaPublisher);
    }

    private static TransacaoEvent evento(String correlationId) {
        return new TransacaoEvent(correlationId, 1L, new BigDecimal("10.00"), TipoMovimento.CREDITO, TipoTransacao.DINHEIRO);
    }

    @Test
    void consumir_devePublicarSucesso_quandoTransacaoEfetivada() {
        TransacaoEvent evt = evento("c1");
        when(saldoService.processar(evt)).thenReturn(Optional.of(new Transacao()));

        transacaoConsumer.consumir(evt);

        ArgumentCaptor<TransacaoProcessadaEvent> captor = ArgumentCaptor.forClass(TransacaoProcessadaEvent.class);
        verify(transacaoProcessadaPublisher).publicar(captor.capture());
        assertThat(captor.getValue().sucesso()).isTrue();
        assertThat(captor.getValue().correlationId()).isEqualTo("c1");
    }

    @Test
    void consumir_naoDevePublicar_quandoDuplicata() {
        TransacaoEvent evt = evento("dup");
        when(saldoService.processar(evt)).thenReturn(Optional.empty());

        transacaoConsumer.consumir(evt);

        verify(transacaoProcessadaPublisher, never()).publicar(any());
    }

    @Test
    void consumir_devePublicarErro_quandoContaInvalida() {
        TransacaoEvent evt = evento("c2");
        when(saldoService.processar(evt)).thenThrow(new ContaInvalidaException("Conta não está ativa"));

        transacaoConsumer.consumir(evt);

        ArgumentCaptor<TransacaoProcessadaEvent> captor = ArgumentCaptor.forClass(TransacaoProcessadaEvent.class);
        verify(transacaoProcessadaPublisher).publicar(captor.capture());
        assertThat(captor.getValue().sucesso()).isFalse();
        assertThat(captor.getValue().motivo()).contains("não está ativa");
    }

    @Test
    void consumir_devePublicarErro_quandoSaldoInsuficiente() {
        TransacaoEvent evt = evento("c3");
        when(saldoService.processar(evt)).thenThrow(new SaldoInsuficienteException("Saldo insuficiente na conta 1"));

        transacaoConsumer.consumir(evt);

        ArgumentCaptor<TransacaoProcessadaEvent> captor = ArgumentCaptor.forClass(TransacaoProcessadaEvent.class);
        verify(transacaoProcessadaPublisher).publicar(captor.capture());
        assertThat(captor.getValue().sucesso()).isFalse();
    }
}
