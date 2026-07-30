package com.demo.saldo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Outbox transacional: linha gravada na mesma transação de banco que efetiva o
 * movimento. O envio pro Kafka é feito à parte pelo OutboxRelay, que só marca
 * enviado_em depois de confirmação do broker — se o processo cair antes disso, a
 * próxima varredura reenvia.
 */
@Getter
@Setter
@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String topico;

    @Column(nullable = false, length = 100)
    private String chave;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;
}
