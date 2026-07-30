package com.demo.saldo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Saldo final de cada dia por conta. A linha do dia corrente é sobrescrita a cada
 * transação efetivada — não existe fechamento em lote, o último valor gravado no dia
 * já É o saldo final daquele dia assim que a data vira. conta_id é só um identificador
 * externo: a conta em si vive no account-service.
 */
@Getter
@Setter
@Entity
@Table(name = "saldo_diario")
public class SaldoDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
