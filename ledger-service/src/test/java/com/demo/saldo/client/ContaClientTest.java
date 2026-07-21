package com.demo.saldo.client;

import com.demo.saldo.dto.ContaResumo;
import com.demo.saldo.exception.ContaInvalidaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ContaClientTest {

    private MockRestServiceServer mockServer;
    private ContaClient contaClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://conta-service");
        mockServer = MockRestServiceServer.bindTo(builder).build();
        contaClient = new ContaClient(builder.build());
    }

    @Test
    void buscarConta_deveRetornarContaResumo_quandoEncontrada() throws Exception {
        String corpo = new ObjectMapper().writeValueAsString(
                new ContaResumo(1L, 1L, "0001-1", "CORRENTE", "ATIVA"));

        mockServer.expect(requestTo("http://conta-service/contas/1"))
                .andRespond(withSuccess(corpo, MediaType.APPLICATION_JSON));

        ContaResumo resultado = contaClient.buscarConta(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo("ATIVA");
        mockServer.verify();
    }

    @Test
    void buscarConta_deveLancarContaInvalida_quandoRecebe404() {
        mockServer.expect(requestTo("http://conta-service/contas/999"))
                .andRespond(withStatus(NOT_FOUND));

        assertThatThrownBy(() -> contaClient.buscarConta(999L))
                .isInstanceOf(ContaInvalidaException.class)
                .hasMessageContaining("999");

        mockServer.verify();
    }
}
