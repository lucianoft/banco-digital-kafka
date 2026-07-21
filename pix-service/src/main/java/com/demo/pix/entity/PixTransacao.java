package com.demo.pix.entity;

import com.demo.pix.event.TipoMovimento;
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
import java.util.UUID;

/**
 * Registro próprio do pix-service pra cada movimentação PIX que ele iniciou —
 * {@code transacaoId} é o id de correlação com o ledger-service (é o mesmo valor
 * que vira {@code correlationId} lá, o equivalente ao EndToEndId do PIX de verdade).
 */
@Getter
@Setter
@Entity
@Table(name = "pix_transacao")
public class PixTransacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transacao_id", nullable = false, unique = true)
    private UUID transacaoId;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false, length = 20)
    private TipoMovimento tipoMovimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPix status;

    @Column(length = 255)
    private String motivo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
