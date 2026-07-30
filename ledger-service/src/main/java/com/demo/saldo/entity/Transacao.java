package com.demo.saldo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ledger só de movimentos efetivados — se a linha existe, o saldo já foi alterado.
 * conta_id é só um identificador externo: a conta em si vive no account-service.
 */
@Getter
@Setter
@Entity
@Table(name = "transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Column(name = "correlation_id", nullable = false, length = 100)
    private String correlationId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    /** CREDITO / DEBITO */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false, length = 20)
    private TipoMovimento tipoMovimento;

    /** DINHEIRO / PIX / TED */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transacao", nullable = false, length = 20)
    private TipoTransacao tipoTransacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
}
