package com.demo.outbox.consumer;

import com.demo.outbox.event.OutboxCdcEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Consome o tópico CDC que o Debezium alimenta a partir da outbox_event do
 * ledger-service (ver comentário na tabela em db/schema.sql) e publica cada evento no
 * tópico de destino gravado nele mesmo (`topico`). Se não conseguir produzir — broker
 * fora do ar, timeout — manda pra "&lt;tópico&gt;-dlq" em vez de derrubar o consumer:
 * assim o offset do tópico CDC avança e a mensagem problemática fica registrada pra
 * inspeção/replay manual, ao invés de travar o relay inteiro reprocessando a mesma
 * falha pra sempre.
 */
@Slf4j
@Component
public class OutboxRelayConsumer {

    private static final Duration TIMEOUT_ENVIO = Duration.ofSeconds(5);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxRelayConsumer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${app.kafka.topic-outbox-cdc}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumir(OutboxCdcEvent evento) {
        try {
            kafkaTemplate.send(evento.topico(), evento.chave(), evento.payload())
                    .get(TIMEOUT_ENVIO.toMillis(), TimeUnit.MILLISECONDS);
            log.info("Outbox event {} publicado no tópico {}", evento.id(), evento.topico());
        } catch (Exception e) {
            log.warn("Falha ao publicar outbox event {} no tópico {}, mandando pra DLQ: {}",
                    evento.id(), evento.topico(), e.getMessage());
            enviarParaDlq(evento);
        }
    }

    private void enviarParaDlq(OutboxCdcEvent evento) {
        String topicoDlq = evento.topico() + "-dlq";
        try {
            kafkaTemplate.send(topicoDlq, evento.chave(), evento.payload())
                    .get(TIMEOUT_ENVIO.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Falha ao publicar outbox event {} na DLQ {}: {}", evento.id(), topicoDlq, e.getMessage());
        }
    }
}
