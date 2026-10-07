package com.demo.pix.service;

import com.demo.pix.event.TipoMovimento;
import com.demo.pix.event.TipoTransacao;
import com.demo.pix.event.TransacaoProcessadaEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class BancoCentralServiceTest {

    private static final TransacaoProcessadaEvent EVENTO = new TransacaoProcessadaEvent(
            UUID.randomUUID().toString(), 1L, TipoMovimento.DEBITO, TipoTransacao.PIX,
            new BigDecimal("10.00"), true, "Transação efetivada com sucesso");

    @Test
    void enviar_deveCompletarSemErro() {
        BancoCentralService service = new BancoCentralService(0L);

        assertThatCode(() -> service.enviar(EVENTO)).doesNotThrowAnyException();
    }

    @Test
    void enviar_deveRestaurarInterruptFlag_quandoThreadForInterrompida() throws InterruptedException {
        BancoCentralService service = new BancoCentralService(10_000L);
        AtomicBoolean flagRestaurada = new AtomicBoolean(false);

        Thread thread = new Thread(() -> {
            service.enviar(EVENTO);
            flagRestaurada.set(Thread.currentThread().isInterrupted());
        });
        thread.start();
        thread.interrupt();
        thread.join(2000);

        assertThat(flagRestaurada.get()).isTrue();
    }
}
