package com.demo.pix.config;

import com.demo.pix.event.TipoTransacao;
import com.demo.pix.event.TransacaoProcessadaEvent;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    /** Mesmo padrão do ledger-service: falha (ex.: erro de desserialização) vai pra "<tópico>-dlq" após 2 retries. */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + "-dlq", record.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }

    /**
     * transacoes-processadas carrega todo tipo (DINHEIRO/TED/PIX). Descarta aqui, no
     * container, tudo que não for PIX — o Spring Boot detecta esse bean automaticamente
     * e aplica na factory padrão, então o método do @KafkaListener nem chega a ser
     * chamado pra DINHEIRO/TED.
     */
    @Bean
    public RecordFilterStrategy<Object, Object> transacaoProcessadaPixFilter() {
        return record -> !(record.value() instanceof TransacaoProcessadaEvent evento
                && evento.tipoTransacao() == TipoTransacao.PIX);
    }
}
