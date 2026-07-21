package com.demo.conta.mapper;

import com.demo.conta.dto.ClienteResponse;
import com.demo.conta.entity.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteResponse toResponse(Cliente cliente);
}
