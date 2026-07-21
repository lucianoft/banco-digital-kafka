package com.demo.conta.dto;

import java.io.Serializable;

/** Contrato exposto via GET /contas/{id} — o ledger-service espera exatamente esse formato. */
public record ContaResumo(Long id, Long clienteId, String numeroConta, String tipo, String status)
        implements Serializable {
}
