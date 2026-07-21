package com.demo.saldo.mapper;

import com.demo.saldo.dto.TransacaoResponse;
import com.demo.saldo.entity.Transacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransacaoMapper {

    TransacaoResponse toResponse(Transacao transacao);
}
