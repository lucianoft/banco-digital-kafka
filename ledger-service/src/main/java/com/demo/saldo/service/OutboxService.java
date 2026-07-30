package com.demo.saldo.service;

import com.demo.saldo.entity.OutboxEvent;
import com.demo.saldo.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Grava o evento a publicar na outbox em vez de mandar pro Kafka na hora. Como o
 * método é @Transactional (REQUIRED), ele entra na transação de quem chamou quando
 * existe uma em andamento — é assim que o SaldoService consegue commitar saldo e
 * evento pendente atomicamente — ou abre uma transação própria quando não há
 * nenhuma ativa (caso do TransacaoConsumer publicando uma rejeição depois do
 * rollback do processamento). O envio de fato é responsabilidade do OutboxRelay.
 */
@Service
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void enfileirar(String topico, String chave, Object evento) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setTopico(topico);
        outboxEvent.setChave(chave);
        outboxEvent.setPayload(serializar(evento));
        outboxEvent.setCriadoEm(LocalDateTime.now());
        outboxEventRepository.save(outboxEvent);
    }

    private String serializar(Object evento) {
        try {
            return objectMapper.writeValueAsString(evento);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar evento para a outbox", e);
        }
    }
}
