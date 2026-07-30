package com.demo.saldo.service;

import com.demo.saldo.entity.OutboxEvent;
import com.demo.saldo.repository.OutboxEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Varre a outbox_event por linhas pendentes e publica no Kafka. Só marca enviado_em
 * depois de confirmação do broker (send().get() com timeout); se falhar ou o broker
 * estiver fora do ar, a linha continua pendente e a próxima varredura tenta de novo
 * — reenvio é seguro porque quem consome transacoes-processadas já lida com
 * duplicidade (correlationId).
 *
 * Polling é o jeito mais simples de implementar o relay, mas em produção com volume
 * alto o SELECT recorrente vira gargalo e a latência fica presa ao intervalo do poll.
 * O caminho natural pra escalar é trocar esta classe por CDC (Debezium lendo o WAL do
 * Postgres e publicando direto), sem precisar mudar outbox_event nem o SaldoService.
 */
@Slf4j
@Component
public class OutboxRelay {

    private static final Duration TIMEOUT_ENVIO = Duration.ofSeconds(5);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> outboxKafkaTemplate;

    public OutboxRelay(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> outboxKafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxKafkaTemplate = outboxKafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${app.outbox.relay-interval-ms:2000}")
    public void publicarPendentes() {
        List<OutboxEvent> pendentes = outboxEventRepository.findTop100ByEnviadoEmIsNullOrderByIdAsc();
        for (OutboxEvent evento : pendentes) {
            try {
                outboxKafkaTemplate.send(evento.getTopico(), evento.getChave(), evento.getPayload())
                        .get(TIMEOUT_ENVIO.toMillis(), TimeUnit.MILLISECONDS);
                evento.setEnviadoEm(LocalDateTime.now());
                outboxEventRepository.save(evento);
            } catch (Exception e) {
                log.warn("Falha ao publicar outbox event {} no tópico {}, tenta de novo na próxima varredura: {}",
                        evento.getId(), evento.getTopico(), e.getMessage());
            }
        }
    }
}
