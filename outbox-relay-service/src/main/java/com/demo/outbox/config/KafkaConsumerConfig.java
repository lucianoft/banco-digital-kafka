package com.demo.outbox.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    /**
     * Mesmo padrão do ledger-service/pix-service: erro técnico ao consumir o tópico CDC
     * (ex.: JSON que o Debezium mandou não bate com OutboxCdcEvent) vai pra
     * "&lt;tópico-cdc&gt;-dlq" após 2 retries. Diferente da DLQ que o OutboxRelayConsumer usa
     * pra falha de produção — aqui é falha de leitura/desserialização da mensagem CDC em
     * si, antes mesmo de saber pra onde republicar.
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + "-dlq", record.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }
}
