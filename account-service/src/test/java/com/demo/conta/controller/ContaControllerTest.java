package com.demo.conta.controller;

import com.demo.conta.dto.ContaResumo;
import com.demo.conta.entity.Conta;
import com.demo.conta.mapper.ContaMapper;
import com.demo.conta.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    @Mock
    private ContaService contaService;
    @Mock
    private ContaMapper contaMapper;

    private ContaController controller;

    @BeforeEach
    void setUp() {
        controller = new ContaController(contaService, contaMapper);
    }

    @Test
    void buscar_deveRetornar200_quandoEncontrada() {
        Conta conta = new Conta();
        ContaResumo resumo = new ContaResumo(1L, 1L, "0001-1", "CORRENTE", "ATIVA");
        when(contaService.buscarPorId(1L)).thenReturn(Optional.of(conta));
        when(contaMapper.toResumo(conta)).thenReturn(resumo);

        ResponseEntity<ContaResumo> resposta = controller.buscar(1L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isEqualTo(resumo);
    }

    @Test
    void buscar_deveRetornar404_quandoNaoEncontrada() {
        when(contaService.buscarPorId(999L)).thenReturn(Optional.empty());

        ResponseEntity<ContaResumo> resposta = controller.buscar(999L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
