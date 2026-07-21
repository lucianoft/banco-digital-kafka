package com.demo.pix.consumer;

import com.demo.pix.entity.PixTransacao;
import com.demo.pix.event.TransacaoProcessadaEvent;
import com.demo.pix.service.BancoCentralService;
import com.demo.pix.service.PixTransacaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * O filtro por tipo (só PIX) já acontece no container via RecordFilterStrategy (ver
 * KafkaConsumerConfig) — aqui só chega evento de PIX. A correlação de verdade é achar o
 * transacaoId na nossa própria tabela pix_transacao.
 */
@Slf4j
@Component
public class TransacaoProcessadaConsumer {

    private final PixTransacaoService pixTransacaoService;
    private final BancoCentralService bancoCentralService;

    public TransacaoProcessadaConsumer(PixTransacaoService pixTransacaoService, BancoCentralService bancoCentralService) {
        this.pixTransacaoService = pixTransacaoService;
        this.bancoCentralService = bancoCentralService;
    }

    @KafkaListener(topics = "${app.kafka.topic-transacoes-processadas}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumir(TransacaoProcessadaEvent evento) {
        UUID transacaoId;
        try {
            transacaoId = UUID.fromString(evento.correlationId());
        } catch (IllegalArgumentException e) {
            log.warn("correlationId {} não é um UUID válido, ignorando (não veio de um PIX iniciado por nós)",
                    evento.correlationId());
            return;
        }

        Optional<PixTransacao> pixTransacao = pixTransacaoService.finalizar(transacaoId, evento.sucesso(), evento.motivo());
        if (pixTransacao.isEmpty()) {
            log.warn("Nenhum PIX conhecido para transacaoId {}, ignorando", transacaoId);
            return;
        }

        if (evento.sucesso()) {
            bancoCentralService.enviar(evento);
        } else {
            log.info("PIX {} não foi efetivado: {}", transacaoId, evento.motivo());
        }
    }
}
