package com.demo.outbox.consumer;

import com.demo.outbox.event.OutboxCdcEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxRelayConsumerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private OutboxRelayConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new OutboxRelayConsumer(kafkaTemplate);
    }

    private static CompletableFuture<SendResult<String, String>> sucesso() {
        return CompletableFuture.completedFuture(mock(SendResult.class));
    }

    private static CompletableFuture<SendResult<String, String>> falha(String motivo) {
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException(motivo));
        return future;
    }

    @Test
    void consumir_devePublicarNoTopicoDeDestino_quandoSucesso() {
        OutboxCdcEvent evento = new OutboxCdcEvent(1L, "transacoes-processadas", "1", "{\"correlationId\":\"c1\"}");
        when(kafkaTemplate.send("transacoes-processadas", "1", evento.payload())).thenReturn(sucesso());

        consumer.consumir(evento);

        verify(kafkaTemplate).send("transacoes-processadas", "1", evento.payload());
        verify(kafkaTemplate, never()).send("transacoes-processadas-dlq", "1", evento.payload());
    }

    @Test
    void consumir_deveMandarParaDlq_quandoFalhaAoPublicarNoTopicoDeDestino() {
        OutboxCdcEvent evento = new OutboxCdcEvent(2L, "transacoes-processadas", "2", "{\"correlationId\":\"c2\"}");
        when(kafkaTemplate.send("transacoes-processadas", "2", evento.payload()))
                .thenReturn(falha("kafka fora do ar"));
        when(kafkaTemplate.send("transacoes-processadas-dlq", "2", evento.payload())).thenReturn(sucesso());

        consumer.consumir(evento);

        verify(kafkaTemplate).send("transacoes-processadas-dlq", "2", evento.payload());
    }

    @Test
    void consumir_naoDeveLancar_quandoFalhaTambemAoPublicarNaDlq() {
        OutboxCdcEvent evento = new OutboxCdcEvent(3L, "transacoes-processadas", "3", "{\"correlationId\":\"c3\"}");
        when(kafkaTemplate.send("transacoes-processadas", "3", evento.payload()))
                .thenReturn(falha("kafka fora do ar"));
        when(kafkaTemplate.send("transacoes-processadas-dlq", "3", evento.payload()))
                .thenReturn(falha("dlq também fora do ar"));

        assertThatCode(() -> consumer.consumir(evento)).doesNotThrowAnyException();
    }
}
