package com.demo.conta.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ClienteResponse(Long id, String nome, String documento, String status, LocalDateTime criadoEm)
        implements Serializable {
}
