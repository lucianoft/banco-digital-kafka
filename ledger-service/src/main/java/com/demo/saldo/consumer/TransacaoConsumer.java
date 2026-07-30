package com.demo.saldo.consumer;

import com.demo.saldo.event.TransacaoEvent;
import com.demo.saldo.event.TransacaoProcessadaEvent;
import com.demo.saldo.exception.ContaInvalidaException;
import com.demo.saldo.exception.SaldoInsuficienteException;
import com.demo.saldo.service.OutboxService;
import com.demo.saldo.service.SaldoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransacaoConsumer {

    private final SaldoService saldoService;
    private final OutboxService outboxService;
    private final String topicoTransacoesProcessadas;

    public TransacaoConsumer(SaldoService saldoService, OutboxService outboxService,
                              @Value("${app.kafka.topic-transacoes-processadas}") String topicoTransacoesProcessadas) {
        this.saldoService = saldoService;
        this.outboxService = outboxService;
        this.topicoTransacoesProcessadas = topicoTransacoesProcessadas;
    }

    /**
     * No caminho de sucesso, o SaldoService já grava o evento de confirmação na
     * outbox dentro da mesma transação que efetiva o saldo. Aqui só sobra tratar a
     * rejeição de negócio: como a exceção derruba a transação de processar(), nada
     * foi persistido pro saldo — mas o evento de rejeição ainda precisa ir pra
     * outbox, então enfileiramos numa transação nova (ver OutboxService).
     */
    @KafkaListener(topics = "${app.kafka.topic-transacoes}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumir(TransacaoEvent evento) {
        log.info("Recebido evento {} da conta {}", evento.correlationId(), evento.contaId());
        try {
            saldoService.processar(evento);
        } catch (ContaInvalidaException | SaldoInsuficienteException e) {
            log.info("Transação {} rejeitada: {}", evento.correlationId(), e.getMessage());
            outboxService.enfileirar(topicoTransacoesProcessadas, evento.contaId().toString(),
                    new TransacaoProcessadaEvent(evento.correlationId(), evento.contaId(), evento.tipoMovimento(),
                            evento.tipoTransacao(), evento.valor(), false, e.getMessage()));
        }
    }
}
