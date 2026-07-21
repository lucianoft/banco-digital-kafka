package com.demo.conta.service;

import com.demo.conta.entity.Cliente;
import com.demo.conta.repository.ClienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService extends CrudService<Cliente, Long> {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    protected JpaRepository<Cliente, Long> getRepository() {
        return clienteRepository;
    }
}
