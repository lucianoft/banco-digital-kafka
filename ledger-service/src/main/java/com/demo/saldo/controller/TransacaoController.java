package com.demo.saldo.controller;

import com.demo.saldo.dto.TransacaoResponse;
import com.demo.saldo.event.TransacaoEvent;
import com.demo.saldo.mapper.TransacaoMapper;
import com.demo.saldo.service.TransacaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TransacaoService transacaoService;
    private final TransacaoMapper transacaoMapper;
    private final String topic;

    public TransacaoController(KafkaTemplate<String, Object> kafkaTemplate, TransacaoService transacaoService,
                                TransacaoMapper transacaoMapper,
                                @Value("${app.kafka.topic-transacoes}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.transacaoService = transacaoService;
        this.transacaoMapper = transacaoMapper;
        this.topic = topic;
    }

    /** Só pra publicar transações de teste no Kafka sem precisar de um producer externo. */
    @PostMapping
    public ResponseEntity<Void> publicar(@RequestBody TransacaoEvent evento) {
        // chave = contaId garante que todas as transações da mesma conta vão pra mesma
        // partição e são processadas em ordem por um único consumer
        kafkaTemplate.send(topic, evento.contaId().toString(), evento);
        return ResponseEntity.accepted().build();
    }

    /** 404 = ainda não efetivada (ou rejeitada) — o processamento é síncrono, então não tem estado intermediário. */
    @GetMapping("/{correlationId}")
    public ResponseEntity<TransacaoResponse> consultar(@PathVariable String correlationId) {
        return transacaoService.buscarPorCorrelationId(correlationId)
                .map(transacaoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
