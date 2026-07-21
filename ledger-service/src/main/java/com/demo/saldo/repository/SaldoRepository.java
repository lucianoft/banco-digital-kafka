package com.demo.saldo.repository;

import com.demo.saldo.entity.Saldo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaldoRepository extends JpaRepository<Saldo, Long> {
}
