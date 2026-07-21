package com.demo.saldo.service;

import com.demo.saldo.entity.SaldoDiario;
import com.demo.saldo.repository.SaldoDiarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SaldoDiarioService extends CrudService<SaldoDiario, Long> {

    private final SaldoDiarioRepository saldoDiarioRepository;

    public SaldoDiarioService(SaldoDiarioRepository saldoDiarioRepository) {
        this.saldoDiarioRepository = saldoDiarioRepository;
    }

    @Override
    protected JpaRepository<SaldoDiario, Long> getRepository() {
        return saldoDiarioRepository;
    }

    /** Upsert: cria a linha do dia na primeira transação e só atualiza o valor nas seguintes. */
    @Transactional
    public void registrar(Long contaId, LocalDate data, BigDecimal valor) {
        SaldoDiario saldoDiario = saldoDiarioRepository.findByContaIdAndData(contaId, data)
                .orElseGet(() -> {
                    SaldoDiario novo = new SaldoDiario();
                    novo.setContaId(contaId);
                    novo.setData(data);
                    return novo;
                });
        saldoDiario.setValor(valor);
        saldoDiario.setAtualizadoEm(LocalDateTime.now());
        saldoDiarioRepository.save(saldoDiario);
    }
}
