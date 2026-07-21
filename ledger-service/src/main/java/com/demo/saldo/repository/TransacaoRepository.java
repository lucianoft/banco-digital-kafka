package com.demo.saldo.repository;

import com.demo.saldo.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    boolean existsByCorrelationId(String correlationId);

    Optional<Transacao> findByCorrelationId(String correlationId);
}
