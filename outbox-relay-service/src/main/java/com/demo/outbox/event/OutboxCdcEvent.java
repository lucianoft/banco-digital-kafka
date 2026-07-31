package com.demo.outbox.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Espelha a linha de outbox_event do ledger-service, já achatada pela SMT
 * ExtractNewRecordState do Debezium — sem envelope before/after/source. `criado_em` e
 * qualquer outro campo extra que o Debezium mande são ignorados: só topico/chave/payload
 * importam pra decidir onde e o quê publicar.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OutboxCdcEvent(Long id, String topico, String chave, String payload) {
}
