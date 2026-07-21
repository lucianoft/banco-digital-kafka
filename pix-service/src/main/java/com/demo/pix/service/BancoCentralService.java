package com.demo.pix.service;

import com.demo.pix.event.TransacaoProcessadaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Simulação fictícia do envio ao SPI (Sistema de Pagamentos Instantâneos) do Banco
 * Central: sem chamada externa nenhuma, só um delay pra imitar a latência real.
 * Só é chamado depois que o ledger-service já confirmou que o débito foi efetivado —
 * mandar pro Bacen antes disso arriscaria anunciar um pagamento que nunca aconteceu.
 */
@Slf4j
@Service
public class BancoCentralService {

    public void enviar(TransacaoProcessadaEvent evento) {
        log.info("Enviando transação {} (conta {}, valor {}) para o Banco Central (SPI)...",
                evento.correlationId(), evento.contaId(), evento.valor());
        simularLatencia();
        log.info("Transação {} confirmada no Banco Central", evento.correlationId());
    }

    private void simularLatencia() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
