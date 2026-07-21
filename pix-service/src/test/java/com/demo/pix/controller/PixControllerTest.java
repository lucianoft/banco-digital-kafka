package com.demo.pix.controller;

import com.demo.pix.dto.PixMovimentacaoRequest;
import com.demo.pix.dto.PixTransacaoResponse;
import com.demo.pix.entity.PixTransacao;
import com.demo.pix.entity.StatusPix;
import com.demo.pix.event.TipoMovimento;
import com.demo.pix.event.TipoTransacao;
import com.demo.pix.event.TransacaoEvent;
import com.demo.pix.mapper.PixTransacaoMapper;
import com.demo.pix.service.PixTransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PixControllerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private PixTransacaoService pixTransacaoService;
    @Mock
    private PixTransacaoMapper pixTransacaoMapper;

    private PixController controller;

    @BeforeEach
    void setUp() {
        controller = new PixController(kafkaTemplate, pixTransacaoService, pixTransacaoMapper, "transacoes");
    }

    @Test
    void receber_deveRegistrarEProduzirEvento_eRetornar202() {
        PixMovimentacaoRequest requisicao = new PixMovimentacaoRequest(1L, new BigDecimal("50.00"), TipoMovimento.DEBITO);
        PixTransacao registrada = new PixTransacao();
        PixTransacaoResponse response = new PixTransacaoResponse(UUID.randomUUID(), 1L, new BigDecimal("50.00"),
                TipoMovimento.DEBITO, StatusPix.ENVIADA, null, LocalDateTime.now(), LocalDateTime.now());

        when(pixTransacaoService.registrar(any(UUID.class), eq(1L), eq(new BigDecimal("50.00")), eq(TipoMovimento.DEBITO)))
                .thenReturn(registrada);
        when(pixTransacaoMapper.toResponse(registrada)).thenReturn(response);

        ResponseEntity<PixTransacaoResponse> resposta = controller.receber(requisicao);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(resposta.getBody()).isEqualTo(response);

        ArgumentCaptor<TransacaoEvent> captor = ArgumentCaptor.forClass(TransacaoEvent.class);
        verify(kafkaTemplate).send(eq("transacoes"), eq("1"), captor.capture());
        assertThat(captor.getValue().tipoTransacao()).isEqualTo(TipoTransacao.PIX);
        assertThat(captor.getValue().tipoMovimento()).isEqualTo(TipoMovimento.DEBITO);
        assertThat(captor.getValue().contaId()).isEqualTo(1L);
    }

    @Test
    void consultar_deveRetornar200_quandoEncontrada() {
        UUID id = UUID.randomUUID();
        PixTransacao pix = new PixTransacao();
        PixTransacaoResponse response = new PixTransacaoResponse(id, 1L, BigDecimal.TEN, TipoMovimento.CREDITO,
                StatusPix.CONFIRMADA, null, LocalDateTime.now(), LocalDateTime.now());

        when(pixTransacaoService.buscarPorTransacaoId(id)).thenReturn(Optional.of(pix));
        when(pixTransacaoMapper.toResponse(pix)).thenReturn(response);

        ResponseEntity<PixTransacaoResponse> resposta = controller.consultar(id);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isEqualTo(response);
    }

    @Test
    void consultar_deveRetornar404_quandoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(pixTransacaoService.buscarPorTransacaoId(id)).thenReturn(Optional.empty());

        ResponseEntity<PixTransacaoResponse> resposta = controller.consultar(id);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
