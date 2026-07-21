package com.demo.saldo.service;

import com.demo.saldo.entity.Transacao;
import com.demo.saldo.repository.TransacaoRepository;
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

/** Também cobre o CrudService genérico, já que TransacaoService só delega pra ele. */
@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() {
        transacaoService = new TransacaoService(transacaoRepository);
    }

    @Test
    void salvar_deveDelegarParaRepository() {
        Transacao transacao = new Transacao();
        when(transacaoRepository.save(transacao)).thenReturn(transacao);

        Transacao resultado = transacaoService.salvar(transacao);

        assertThat(resultado).isSameAs(transacao);
        verify(transacaoRepository).save(transacao);
    }

    @Test
    void buscarPorId_deveDelegarParaRepository() {
        Transacao transacao = new Transacao();
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        assertThat(transacaoService.buscarPorId(1L)).contains(transacao);
    }

    @Test
    void listarTodos_deveDelegarParaRepository() {
        when(transacaoRepository.findAll()).thenReturn(List.of(new Transacao()));

        assertThat(transacaoService.listarTodos()).hasSize(1);
    }

    @Test
    void existePorId_deveDelegarParaRepository() {
        when(transacaoRepository.existsById(1L)).thenReturn(true);

        assertThat(transacaoService.existePorId(1L)).isTrue();
    }

    @Test
    void deletar_deveDelegarParaRepository() {
        transacaoService.deletar(1L);

        verify(transacaoRepository).deleteById(1L);
    }

    @Test
    void buscarPorCorrelationId_deveDelegarParaRepository() {
        Transacao transacao = new Transacao();
        when(transacaoRepository.findByCorrelationId("c1")).thenReturn(Optional.of(transacao));

        assertThat(transacaoService.buscarPorCorrelationId("c1")).contains(transacao);
    }
}
