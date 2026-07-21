package com.demo.conta.service;

import com.demo.conta.entity.Conta;
import com.demo.conta.repository.ContaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ContaService extends CrudService<Conta, Long> {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @Override
    protected JpaRepository<Conta, Long> getRepository() {
        return contaRepository;
    }
}
