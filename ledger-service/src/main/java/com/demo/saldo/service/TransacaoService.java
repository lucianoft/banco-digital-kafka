package com.demo.saldo.service;

import com.demo.saldo.entity.Transacao;
import com.demo.saldo.repository.TransacaoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransacaoService extends CrudService<Transacao, Long> {

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    protected JpaRepository<Transacao, Long> getRepository() {
        return transacaoRepository;
    }

    public Optional<Transacao> buscarPorCorrelationId(String correlationId) {
        return transacaoRepository.findByCorrelationId(correlationId);
    }
}
