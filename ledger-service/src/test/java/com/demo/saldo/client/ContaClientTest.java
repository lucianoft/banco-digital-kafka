package com.demo.saldo.client;

import com.demo.saldo.dto.ContaResumo;
import com.demo.saldo.exception.ContaInvalidaException;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class ContaClientTest {

    private ContaClient contaClient;
    private ContaClient contaClientComTimeout;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wm) {
        contaClient = new ContaClient(
                RestClient.builder().baseUrl(wm.getHttpBaseUrl()).build());

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(200);
        contaClientComTimeout = new ContaClient(
                RestClient.builder().baseUrl(wm.getHttpBaseUrl()).requestFactory(factory).build());
    }

    @Test
    void buscarConta_deveRetornarContaResumo_quandoEncontrada() {
        stubFor(get(urlPathEqualTo("/contas/1"))
                .willReturn(okJson("""
                        {"id":1,"clienteId":1,"numeroConta":"0001-1","tipo":"CORRENTE","status":"ATIVA"}
                        """)));

        ContaResumo resultado = contaClient.buscarConta(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo("ATIVA");
        verify(getRequestedFor(urlPathEqualTo("/contas/1")));
    }

    @Test
    void buscarConta_deveLancarContaInvalida_quandoRecebe404() {
        stubFor(get(urlPathEqualTo("/contas/999"))
                .willReturn(notFound()));

        assertThatThrownBy(() -> contaClient.buscarConta(999L))
                .isInstanceOf(ContaInvalidaException.class)
                .hasMessageContaining("999");

        verify(getRequestedFor(urlPathEqualTo("/contas/999")));
    }

    @Test
    void buscarConta_devePropagar_quandoServidorRetorna500() {
        stubFor(get(urlPathEqualTo("/contas/2"))
                .willReturn(serverError()));

        assertThatThrownBy(() -> contaClient.buscarConta(2L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void buscarConta_devePropagar_quandoTimeoutDeRede() {
        stubFor(get(urlPathEqualTo("/contas/3"))
                .willReturn(ok().withFixedDelay(500)));

        assertThatThrownBy(() -> contaClientComTimeout.buscarConta(3L))
                .isInstanceOf(ResourceAccessException.class);
    }
}
