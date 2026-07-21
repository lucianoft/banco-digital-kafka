package com.demo.saldo.repository;

import com.demo.saldo.entity.SaldoDiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SaldoDiarioRepository extends JpaRepository<SaldoDiario, Long> {

    Optional<SaldoDiario> findByContaIdAndData(Long contaId, LocalDate data);
}
