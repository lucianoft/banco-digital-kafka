package com.demo.saldo.service;

import com.demo.saldo.entity.SaldoDiario;
import com.demo.saldo.repository.SaldoDiarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaldoDiarioServiceTest {

    @Mock
    private SaldoDiarioRepository saldoDiarioRepository;

    private SaldoDiarioService saldoDiarioService;

    @BeforeEach
    void setUp() {
        saldoDiarioService = new SaldoDiarioService(saldoDiarioRepository);
    }

    @Test
    void registrar_deveCriarNovaLinha_quandoNaoExisteRegistroDoDia() {
        LocalDate hoje = LocalDate.now();
        when(saldoDiarioRepository.findByContaIdAndData(1L, hoje)).thenReturn(Optional.empty());

        saldoDiarioService.registrar(1L, hoje, new BigDecimal("100.00"));

        ArgumentCaptor<SaldoDiario> captor = ArgumentCaptor.forClass(SaldoDiario.class);
        verify(saldoDiarioRepository).save(captor.capture());
        SaldoDiario salvo = captor.getValue();
        assertThat(salvo.getContaId()).isEqualTo(1L);
        assertThat(salvo.getData()).isEqualTo(hoje);
        assertThat(salvo.getValor()).isEqualByComparingTo("100.00");
    }

    @Test
    void registrar_deveAtualizarValor_quandoJaExisteRegistroDoDia() {
        LocalDate hoje = LocalDate.now();
        SaldoDiario existente = new SaldoDiario();
        existente.setContaId(1L);
        existente.setData(hoje);
        existente.setValor(new BigDecimal("100.00"));
        when(saldoDiarioRepository.findByContaIdAndData(1L, hoje)).thenReturn(Optional.of(existente));

        saldoDiarioService.registrar(1L, hoje, new BigDecimal("250.00"));

        ArgumentCaptor<SaldoDiario> captor = ArgumentCaptor.forClass(SaldoDiario.class);
        verify(saldoDiarioRepository).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(existente);
        assertThat(captor.getValue().getValor()).isEqualByComparingTo("250.00");
    }
}
