package com.demo.saldo.service;

import com.demo.saldo.event.TransacaoProcessadaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransacaoProcessadaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public TransacaoProcessadaPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                         @Value("${app.kafka.topic-transacoes-processadas}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publicar(TransacaoProcessadaEvent evento) {
        kafkaTemplate.send(topic, evento.contaId().toString(), evento);
    }
}
