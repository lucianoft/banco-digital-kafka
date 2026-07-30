package com.demo.saldo.dto;

import java.io.Serializable;

/**
 * Espelha com.demo.conta.dto.ContaResumo do account-service — é o corpo que vem de
 * GET /contas/{id}, cacheado no Redis pelo ContaClient (cache-aside).
 */
public record ContaResumo(Long id, Long clienteId, String numeroConta, String tipo, String status)
        implements Serializable {
}
