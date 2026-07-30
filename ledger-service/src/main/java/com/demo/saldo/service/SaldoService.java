package com.demo.saldo.service;

import com.demo.saldo.client.ContaClient;
import com.demo.saldo.dto.ContaResumo;
import com.demo.saldo.entity.Saldo;
import com.demo.saldo.entity.Transacao;
import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.event.TransacaoEvent;
import com.demo.saldo.event.TransacaoProcessadaEvent;
import com.demo.saldo.exception.ContaInvalidaException;
import com.demo.saldo.exception.SaldoInsuficienteException;
import com.demo.saldo.repository.SaldoRepository;
import com.demo.saldo.repository.TransacaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Core bancário: só efetiva movimentos e controla o saldo. Não sabe nada sobre PIX
 * nem sobre nenhum tipo específico — quem quiser orquestrar algo assíncrono em cima
 * disso (como o pix-service falando com o Banco Central) reage ao resultado publicado
 * em transacoes-processadas, não mexe direto aqui.
 */
@Slf4j
@Service
public class SaldoService {

    private static final String STATUS_CONTA_ATIVA = "ATIVA";

    private final SaldoRepository saldoRepository;
    private final TransacaoRepository transacaoRepository;
    private final ContaClient contaClient;
    private final SaldoDiarioService saldoDiarioService;
    private final OutboxService outboxService;
    private final String topicoTransacoesProcessadas;

    public SaldoService(SaldoRepository saldoRepository, TransacaoRepository transacaoRepository,
                         ContaClient contaClient, SaldoDiarioService saldoDiarioService,
                         OutboxService outboxService,
                         @Value("${app.kafka.topic-transacoes-processadas}") String topicoTransacoesProcessadas) {
        this.saldoRepository = saldoRepository;
        this.transacaoRepository = transacaoRepository;
        this.contaClient = contaClient;
        this.saldoDiarioService = saldoDiarioService;
        this.outboxService = outboxService;
        this.topicoTransacoesProcessadas = topicoTransacoesProcessadas;
    }

    /**
     * Efetiva o movimento na hora (crédito ou débito) ou lança uma exceção de negócio
     * (conta inválida / saldo insuficiente) sem persistir nada — quem chama decide o
     * que fazer com a rejeição (ver TransacaoConsumer, que enfileira o resultado na
     * outbox). No caminho de sucesso, o evento de confirmação já é gravado na outbox
     * aqui dentro, na mesma transação que efetiva o saldo — outbox transacional, pra
     * não correr o risco de commitar o saldo e nunca publicar o evento. Duplicata de
     * correlationId é ignorada silenciosamente.
     */
    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 5,
            backoff = @Backoff(delay = 50, multiplier = 2))
    @Transactional
    public Optional<Transacao> processar(TransacaoEvent evento) {
        if (transacaoRepository.existsByCorrelationId(evento.correlationId())) {
            log.info("Evento {} já processado, ignorando (idempotência)", evento.correlationId());
            return Optional.empty();
        }

        ContaResumo conta = contaClient.buscarConta(evento.contaId());
        if (!STATUS_CONTA_ATIVA.equals(conta.status())) {
            throw new ContaInvalidaException("Conta " + conta.id() + " não está ativa");
        }

        Saldo saldo = saldoRepository.findById(evento.contaId())
                .orElseThrow(() -> new ContaInvalidaException("Saldo não encontrado para a conta " + evento.contaId()));

        saldo.setValor(aplicarMovimento(saldo.getValor(), evento));
        saldo.setAtualizadoEm(LocalDateTime.now());

        saldoDiarioService.registrar(saldo.getContaId(), LocalDate.now(), saldo.getValor());

        Transacao transacao = new Transacao();
        transacao.setContaId(saldo.getContaId());
        transacao.setCorrelationId(evento.correlationId());
        transacao.setValor(evento.valor());
        transacao.setTipoMovimento(evento.tipoMovimento());
        transacao.setTipoTransacao(evento.tipoTransacao());
        transacao.setCriadoEm(LocalDateTime.now());
        transacaoRepository.save(transacao);

        outboxService.enfileirar(topicoTransacoesProcessadas, evento.contaId().toString(),
                new TransacaoProcessadaEvent(evento.correlationId(), evento.contaId(), evento.tipoMovimento(),
                        evento.tipoTransacao(), evento.valor(), true, "Transação efetivada com sucesso"));

        return Optional.of(transacao);
    }

    private BigDecimal aplicarMovimento(BigDecimal saldoAtual, TransacaoEvent evento) {
        BigDecimal novoSaldo = TipoMovimento.CREDITO.equals(evento.tipoMovimento())
                ? saldoAtual.add(evento.valor())
                : saldoAtual.subtract(evento.valor());

        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta " + evento.contaId());
        }
        return novoSaldo;
    }
}
