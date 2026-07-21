package com.demo.saldo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** conta_id é só um identificador externo aqui — a conta em si vive no conta-service, em outro banco. */
@Getter
@Setter
@Entity
@Table(name = "saldo")
public class Saldo {

    @Id
    @Column(name = "conta_id")
    private Long contaId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    // sem setter: coluna de lock otimista, controlada pelo Hibernate via @Version
    @Setter(lombok.AccessLevel.NONE)
    @Version
    @Column(nullable = false)
    private Long versao;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
