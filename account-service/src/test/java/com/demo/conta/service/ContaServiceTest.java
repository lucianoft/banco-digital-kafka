package com.demo.conta.service;

import com.demo.conta.entity.Conta;
import com.demo.conta.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    private ContaService contaService;

    @BeforeEach
    void setUp() {
        contaService = new ContaService(contaRepository);
    }

    @Test
    void buscarPorId_deveDelegarParaRepository() {
        Conta conta = new Conta();
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        assertThat(contaService.buscarPorId(1L)).contains(conta);
    }
}
