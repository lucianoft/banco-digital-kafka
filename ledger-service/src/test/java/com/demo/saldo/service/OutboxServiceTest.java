package com.demo.saldo.service;

import com.demo.saldo.entity.OutboxEvent;
import com.demo.saldo.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;
    @Mock
    private ObjectMapper objectMapper;

    private OutboxService outboxService;

    @BeforeEach
    void setUp() {
        outboxService = new OutboxService(outboxEventRepository, objectMapper);
    }

    @Test
    void enfileirar_deveSalvarOutboxEventComCamposCorretos() throws Exception {
        Object payload = new Object();
        when(objectMapper.writeValueAsString(payload)).thenReturn("{\"ok\":true}");

        outboxService.enfileirar("topico-teste", "chave-1", payload);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());
        OutboxEvent salvo = captor.getValue();
        assertThat(salvo.getTopico()).isEqualTo("topico-teste");
        assertThat(salvo.getChave()).isEqualTo("chave-1");
        assertThat(salvo.getPayload()).isEqualTo("{\"ok\":true}");
        assertThat(salvo.getCriadoEm()).isNotNull();
    }

    @Test
    void enfileirar_deveLancarIllegalState_quandoSerializacaoFalhar() throws Exception {
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("erro de serialização") {});

        assertThatThrownBy(() -> outboxService.enfileirar("topico", "chave", new Object()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("serializar");
    }
}
