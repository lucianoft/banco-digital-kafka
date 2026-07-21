package com.demo.pix.repository;

import com.demo.pix.entity.PixTransacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PixTransacaoRepository extends JpaRepository<PixTransacao, Long> {

    Optional<PixTransacao> findByTransacaoId(UUID transacaoId);
}
