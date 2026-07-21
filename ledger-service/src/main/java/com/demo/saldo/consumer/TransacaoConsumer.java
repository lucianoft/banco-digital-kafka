package com.demo.saldo.consumer;

import com.demo.saldo.entity.Transacao;
import com.demo.saldo.event.TransacaoEvent;
import com.demo.saldo.event.TransacaoProcessadaEvent;
import com.demo.saldo.exception.ContaInvalidaException;
import com.demo.saldo.exception.SaldoInsuficienteException;
import com.demo.saldo.service.SaldoService;
import com.demo.saldo.service.TransacaoProcessadaPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class TransacaoConsumer {

    private final SaldoService saldoService;
    private final TransacaoProcessadaPublisher transacaoProcessadaPublisher;

    public TransacaoConsumer(SaldoService saldoService, TransacaoProcessadaPublisher transacaoProcessadaPublisher) {
        this.saldoService = saldoService;
        this.transacaoProcessadaPublisher = transacaoProcessadaPublisher;
    }

    @KafkaListener(topics = "${app.kafka.topic-transacoes}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumir(TransacaoEvent evento) {
        log.info("Recebido evento {} da conta {}", evento.correlationId(), evento.contaId());
        try {
            Optional<Transacao> transacao = saldoService.processar(evento);
            transacao.ifPresent(t -> transacaoProcessadaPublisher.publicar(
                    new TransacaoProcessadaEvent(evento.correlationId(), evento.contaId(), evento.tipoMovimento(),
                            evento.tipoTransacao(), evento.valor(), true, "Transação efetivada com sucesso")));
        } catch (ContaInvalidaException | SaldoInsuficienteException e) {
            log.info("Transação {} rejeitada: {}", evento.correlationId(), e.getMessage());
            transacaoProcessadaPublisher.publicar(new TransacaoProcessadaEvent(evento.correlationId(), evento.contaId(),
                    evento.tipoMovimento(), evento.tipoTransacao(), evento.valor(), false, e.getMessage()));
        }
    }
}
