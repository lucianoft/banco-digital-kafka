package com.demo.saldo.consumer;

import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;
import com.demo.saldo.entity.Transacao;
import com.demo.saldo.event.TransacaoEvent;
import com.demo.saldo.event.TransacaoProcessadaEvent;
import com.demo.saldo.exception.ContaInvalidaException;
import com.demo.saldo.exception.SaldoInsuficienteException;
import com.demo.saldo.service.OutboxService;
import com.demo.saldo.service.SaldoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoConsumerTest {

    private static final String TOPICO_TRANSACOES_PROCESSADAS = "transacoes-processadas";

    @Mock
    private SaldoService saldoService;
    @Mock
    private OutboxService outboxService;

    private TransacaoConsumer transacaoConsumer;

    @BeforeEach
    void setUp() {
        transacaoConsumer = new TransacaoConsumer(saldoService, outboxService, TOPICO_TRANSACOES_PROCESSADAS);
    }

    private static TransacaoEvent evento(String correlationId) {
        return new TransacaoEvent(correlationId, 1L, new BigDecimal("10.00"), TipoMovimento.CREDITO, TipoTransacao.DINHEIRO);
    }

    @Test
    void consumir_naoDeveTocarNaOutbox_quandoTransacaoEfetivada() {
        TransacaoEvent evt = evento("c1");
        when(saldoService.processar(evt)).thenReturn(Optional.of(new Transacao()));

        transacaoConsumer.consumir(evt);

        verifyNoInteractions(outboxService);
    }

    @Test
    void consumir_naoDeveTocarNaOutbox_quandoDuplicata() {
        TransacaoEvent evt = evento("dup");
        when(saldoService.processar(evt)).thenReturn(Optional.empty());

        transacaoConsumer.consumir(evt);

        verifyNoInteractions(outboxService);
    }

    @Test
    void consumir_deveEnfileirarErro_quandoContaInvalida() {
        TransacaoEvent evt = evento("c2");
        when(saldoService.processar(evt)).thenThrow(new ContaInvalidaException("Conta não está ativa"));

        transacaoConsumer.consumir(evt);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(outboxService).enfileirar(eq(TOPICO_TRANSACOES_PROCESSADAS), eq("1"), captor.capture());
        TransacaoProcessadaEvent processado = (TransacaoProcessadaEvent) captor.getValue();
        assertThat(processado.sucesso()).isFalse();
        assertThat(processado.motivo()).contains("não está ativa");
    }

    @Test
    void consumir_deveEnfileirarErro_quandoSaldoInsuficiente() {
        TransacaoEvent evt = evento("c3");
        when(saldoService.processar(evt)).thenThrow(new SaldoInsuficienteException("Saldo insuficiente na conta 1"));

        transacaoConsumer.consumir(evt);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(outboxService).enfileirar(eq(TOPICO_TRANSACOES_PROCESSADAS), eq("1"), captor.capture());
        TransacaoProcessadaEvent processado = (TransacaoProcessadaEvent) captor.getValue();
        assertThat(processado.sucesso()).isFalse();
    }
}
