package com.demo.conta.mapper;

import com.demo.conta.dto.ContaResumo;
import com.demo.conta.entity.Cliente;
import com.demo.conta.entity.Conta;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-30T15:34:34-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class ContaMapperImpl implements ContaMapper {

    @Override
    public ContaResumo toResumo(Conta conta) {
        if ( conta == null ) {
            return null;
        }

        Long clienteId = null;
        Long id = null;
        String numeroConta = null;
        String tipo = null;
        String status = null;

        clienteId = contaClienteId( conta );
        id = conta.getId();
        numeroConta = conta.getNumeroConta();
        tipo = conta.getTipo();
        status = conta.getStatus();

        ContaResumo contaResumo = new ContaResumo( id, clienteId, numeroConta, tipo, status );

        return contaResumo;
    }

    private Long contaClienteId(Conta conta) {
        Cliente cliente = conta.getCliente();
        if ( cliente == null ) {
            return null;
        }
        return cliente.getId();
    }
}
