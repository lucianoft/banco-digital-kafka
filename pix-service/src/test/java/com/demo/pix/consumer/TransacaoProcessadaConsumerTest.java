package com.demo.pix.consumer;

import com.demo.pix.entity.PixTransacao;
import com.demo.pix.event.TipoMovimento;
import com.demo.pix.event.TipoTransacao;
import com.demo.pix.event.TransacaoProcessadaEvent;
import com.demo.pix.service.BancoCentralService;
import com.demo.pix.service.PixTransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoProcessadaConsumerTest {

    @Mock
    private PixTransacaoService pixTransacaoService;
    @Mock
    private BancoCentralService bancoCentralService;

    private TransacaoProcessadaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new TransacaoProcessadaConsumer(pixTransacaoService, bancoCentralService);
    }

    @Test
    void consumir_deveChamarBancoCentral_quandoSucessoEConhecido() {
        UUID id = UUID.randomUUID();
        TransacaoProcessadaEvent evento = new TransacaoProcessadaEvent(id.toString(), 1L, TipoMovimento.DEBITO,
                TipoTransacao.PIX, new BigDecimal("10.00"), true, "Transação efetivada com sucesso");
        when(pixTransacaoService.finalizar(id, true, evento.motivo())).thenReturn(Optional.of(new PixTransacao()));

        consumer.consumir(evento);

        verify(bancoCentralService).enviar(evento);
    }

    @Test
    void consumir_naoDeveChamarBancoCentral_quandoFalha() {
        UUID id = UUID.randomUUID();
        TransacaoProcessadaEvent evento = new TransacaoProcessadaEvent(id.toString(), 1L, TipoMovimento.DEBITO,
                TipoTransacao.PIX, new BigDecimal("10.00"), false, "Saldo insuficiente");
        when(pixTransacaoService.finalizar(id, false, evento.motivo())).thenReturn(Optional.of(new PixTransacao()));

        consumer.consumir(evento);

        verify(bancoCentralService, never()).enviar(any());
    }

    @Test
    void consumir_naoDeveChamarBancoCentral_quandoTransacaoDesconhecida() {
        UUID id = UUID.randomUUID();
        TransacaoProcessadaEvent evento = new TransacaoProcessadaEvent(id.toString(), 1L, TipoMovimento.DEBITO,
                TipoTransacao.PIX, new BigDecimal("10.00"), true, "sucesso");
        when(pixTransacaoService.finalizar(id, true, evento.motivo())).thenReturn(Optional.empty());

        consumer.consumir(evento);

        verify(bancoCentralService, never()).enviar(any());
    }

    @Test
    void consumir_deveIgnorarSemErro_quandoCorrelationIdNaoEUuid() {
        TransacaoProcessadaEvent evento = new TransacaoProcessadaEvent("nao-e-uuid", 1L, TipoMovimento.DEBITO,
                TipoTransacao.PIX, new BigDecimal("10.00"), true, "sucesso");

        consumer.consumir(evento);

        verifyNoInteractions(pixTransacaoService, bancoCentralService);
    }
}
