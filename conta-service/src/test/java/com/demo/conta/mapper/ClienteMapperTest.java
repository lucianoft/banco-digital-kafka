package com.demo.conta.mapper;

import com.demo.conta.dto.ClienteResponse;
import com.demo.conta.entity.Cliente;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteMapperTest {

    private final ClienteMapper mapper = Mappers.getMapper(ClienteMapper.class);

    @Test
    void toResponse_deveMapearTodosOsCampos() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Souza");
        cliente.setDocumento("12345678900");
        cliente.setStatus("ATIVO");
        cliente.setCriadoEm(LocalDateTime.of(2026, 1, 1, 8, 0));

        ClienteResponse response = mapper.toResponse(cliente);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Maria Souza");
        assertThat(response.documento()).isEqualTo("12345678900");
        assertThat(response.status()).isEqualTo("ATIVO");
        assertThat(response.criadoEm()).isEqualTo(LocalDateTime.of(2026, 1, 1, 8, 0));
    }
}
