package com.demo.saldo.controller;

import com.demo.saldo.dto.TransacaoResponse;
import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;
import com.demo.saldo.entity.Transacao;
import com.demo.saldo.event.TransacaoEvent;
import com.demo.saldo.mapper.TransacaoMapper;
import com.demo.saldo.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private TransacaoService transacaoService;
    @Mock
    private TransacaoMapper transacaoMapper;

    private TransacaoController controller;

    @BeforeEach
    void setUp() {
        controller = new TransacaoController(kafkaTemplate, transacaoService, transacaoMapper, "transacoes");
    }

    @Test
    void publicar_deveEnviarParaKafkaComChaveContaId_eRetornar202() {
        TransacaoEvent evento = new TransacaoEvent("c1", 1L, new BigDecimal("10.00"),
                TipoMovimento.CREDITO, TipoTransacao.DINHEIRO);

        ResponseEntity<Void> resposta = controller.publicar(evento);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(kafkaTemplate).send("transacoes", "1", evento);
    }

    @Test
    void consultar_deveRetornar200_quandoEncontrada() {
        Transacao transacao = new Transacao();
        TransacaoResponse response = new TransacaoResponse("c1", new BigDecimal("10.00"),
                TipoMovimento.CREDITO, TipoTransacao.DINHEIRO, LocalDateTime.now());

        when(transacaoService.buscarPorCorrelationId("c1")).thenReturn(Optional.of(transacao));
        when(transacaoMapper.toResponse(transacao)).thenReturn(response);

        ResponseEntity<TransacaoResponse> resposta = controller.consultar("c1");

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isEqualTo(response);
    }

    @Test
    void consultar_deveRetornar404_quandoNaoEncontrada() {
        when(transacaoService.buscarPorCorrelationId("naoexiste")).thenReturn(Optional.empty());

        ResponseEntity<TransacaoResponse> resposta = controller.consultar("naoexiste");

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
