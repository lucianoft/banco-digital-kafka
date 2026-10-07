package com.demo.conta.service;

import com.demo.conta.entity.Cliente;
import com.demo.conta.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService(clienteRepository);
    }

    @Test
    void salvar_deveDelegarParaRepository() {
        Cliente cliente = new Cliente();
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        assertThat(clienteService.salvar(cliente)).isSameAs(cliente);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void buscarPorId_deveDelegarParaRepository() {
        Cliente cliente = new Cliente();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThat(clienteService.buscarPorId(1L)).contains(cliente);
    }

    @Test
    void listarTodos_deveDelegarParaRepository() {
        when(clienteRepository.findAll()).thenReturn(List.of(new Cliente()));

        assertThat(clienteService.listarTodos()).hasSize(1);
    }

    @Test
    void existePorId_deveDelegarParaRepository() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        assertThat(clienteService.existePorId(1L)).isTrue();
    }

    @Test
    void deletar_deveDelegarParaRepository() {
        clienteService.deletar(1L);

        verify(clienteRepository).deleteById(1L);
    }
}
