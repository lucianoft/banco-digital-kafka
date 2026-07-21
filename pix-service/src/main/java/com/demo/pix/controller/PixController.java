package com.demo.pix.controller;

import com.demo.pix.dto.PixMovimentacaoRequest;
import com.demo.pix.dto.PixTransacaoResponse;
import com.demo.pix.entity.PixTransacao;
import com.demo.pix.event.TipoTransacao;
import com.demo.pix.event.TransacaoEvent;
import com.demo.pix.mapper.PixTransacaoMapper;
import com.demo.pix.service.PixTransacaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Porta de entrada de uma movimentação PIX — recebe, registra localmente e produz pro tópico do core bancário. */
@RestController
@RequestMapping("/pix")
public class PixController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PixTransacaoService pixTransacaoService;
    private final PixTransacaoMapper pixTransacaoMapper;
    private final String topicTransacoes;

    public PixController(KafkaTemplate<String, Object> kafkaTemplate, PixTransacaoService pixTransacaoService,
                          PixTransacaoMapper pixTransacaoMapper,
                          @Value("${app.kafka.topic-transacoes}") String topicTransacoes) {
        this.kafkaTemplate = kafkaTemplate;
        this.pixTransacaoService = pixTransacaoService;
        this.pixTransacaoMapper = pixTransacaoMapper;
        this.topicTransacoes = topicTransacoes;
    }

    @PostMapping
    public ResponseEntity<PixTransacaoResponse> receber(@RequestBody PixMovimentacaoRequest requisicao) {
        // o transacaoId é o id de correlação com o ledger-service (vira o correlationId lá) —
        // gerado aqui, não aceito do chamador, pra garantir unicidade de verdade
        UUID transacaoId = UUID.randomUUID();
        PixTransacao registrada = pixTransacaoService.registrar(transacaoId, requisicao.contaId(),
                requisicao.valor(), requisicao.tipoMovimento());

        TransacaoEvent evento = new TransacaoEvent(transacaoId.toString(), requisicao.contaId(), requisicao.valor(),
                requisicao.tipoMovimento(), TipoTransacao.PIX);
        kafkaTemplate.send(topicTransacoes, requisicao.contaId().toString(), evento);

        return ResponseEntity.accepted().body(pixTransacaoMapper.toResponse(registrada));
    }

    @GetMapping("/{transacaoId}")
    public ResponseEntity<PixTransacaoResponse> consultar(@PathVariable UUID transacaoId) {
        return pixTransacaoService.buscarPorTransacaoId(transacaoId)
                .map(pixTransacaoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
