package com.demo.conta.controller;

import com.demo.conta.dto.ClienteResponse;
import com.demo.conta.entity.Cliente;
import com.demo.conta.mapper.ClienteMapper;
import com.demo.conta.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;
    @Mock
    private ClienteMapper clienteMapper;

    private ClienteController controller;

    @BeforeEach
    void setUp() {
        controller = new ClienteController(clienteService, clienteMapper);
    }

    @Test
    void buscar_deveRetornar200_quandoEncontrado() {
        Cliente cliente = new Cliente();
        ClienteResponse response = new ClienteResponse(1L, "Maria Souza", "12345678900", "ATIVO", LocalDateTime.now());
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponse(cliente)).thenReturn(response);

        ResponseEntity<ClienteResponse> resposta = controller.buscar(1L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isEqualTo(response);
    }

    @Test
    void buscar_deveRetornar404_quandoNaoEncontrado() {
        when(clienteService.buscarPorId(999L)).thenReturn(Optional.empty());

        ResponseEntity<ClienteResponse> resposta = controller.buscar(999L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
