package com.demo.conta.mapper;

import com.demo.conta.dto.ContaResumo;
import com.demo.conta.entity.Cliente;
import com.demo.conta.entity.Conta;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ContaMapperTest {

    private final ContaMapper mapper = Mappers.getMapper(ContaMapper.class);

    @Test
    void toResumo_deveMapearClienteIdAPartirDoRelacionamento() {
        Cliente cliente = new Cliente();
        cliente.setId(7L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setCliente(cliente);
        conta.setNumeroConta("0001-1");
        conta.setTipo("CORRENTE");
        conta.setStatus("ATIVA");

        ContaResumo resumo = mapper.toResumo(conta);

        assertThat(resumo.id()).isEqualTo(1L);
        assertThat(resumo.clienteId()).isEqualTo(7L);
        assertThat(resumo.numeroConta()).isEqualTo("0001-1");
        assertThat(resumo.tipo()).isEqualTo("CORRENTE");
        assertThat(resumo.status()).isEqualTo("ATIVA");
    }
}
