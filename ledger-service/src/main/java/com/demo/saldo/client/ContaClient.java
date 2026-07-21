package com.demo.saldo.client;

import com.demo.saldo.dto.ContaResumo;
import com.demo.saldo.exception.ContaInvalidaException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP do conta-service — cadastro (cliente/conta) não é mais dado local, vive
 * em outro serviço/banco. O cache-aside no Redis (já existia antes da extração) evita
 * bater no conta-service a cada transação da mesma conta.
 */
@Component
public class ContaClient {

    private final RestClient restClient;

    public ContaClient(RestClient contaServiceRestClient) {
        this.restClient = contaServiceRestClient;
    }

    @Cacheable(value = "contas", key = "#contaId")
    public ContaResumo buscarConta(Long contaId) {
        try {
            return restClient.get()
                    .uri("/contas/{id}", contaId)
                    .retrieve()
                    .body(ContaResumo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ContaInvalidaException("Conta não encontrada: " + contaId);
        }
    }
}
