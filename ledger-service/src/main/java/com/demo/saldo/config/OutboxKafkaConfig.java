package com.demo.saldo.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

/**
 * Producer separado do KafkaTemplate<String, Object> autoconfigurado (que serializa
 * valor com JsonSerializer). O OutboxRelay já guarda o payload como JSON pronto na
 * outbox_event — reenviar isso por um template que serializa com Jackson de novo
 * geraria uma string JSON escapada dentro de outra. Esse aqui manda o payload como
 * está, com StringSerializer em chave e valor.
 */
@Configuration
public class OutboxKafkaConfig {

    @Bean
    public ProducerFactory<String, String> outboxProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> outboxKafkaTemplate(ProducerFactory<String, String> outboxProducerFactory) {
        return new KafkaTemplate<>(outboxProducerFactory);
    }
}
