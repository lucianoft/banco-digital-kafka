package com.demo.conta.mapper;

import com.demo.conta.dto.ClienteResponse;
import com.demo.conta.entity.Cliente;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-18T18:02:34+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteResponse toResponse(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        Long id = null;
        String nome = null;
        String documento = null;
        String status = null;
        LocalDateTime criadoEm = null;

        id = cliente.getId();
        nome = cliente.getNome();
        documento = cliente.getDocumento();
        status = cliente.getStatus();
        criadoEm = cliente.getCriadoEm();

        ClienteResponse clienteResponse = new ClienteResponse( id, nome, documento, status, criadoEm );

        return clienteResponse;
    }
}
