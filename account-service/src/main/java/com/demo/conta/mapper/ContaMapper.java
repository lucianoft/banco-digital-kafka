package com.demo.conta.mapper;

import com.demo.conta.dto.ContaResumo;
import com.demo.conta.entity.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContaMapper {

    @Mapping(target = "clienteId", source = "cliente.id")
    ContaResumo toResumo(Conta conta);
}
