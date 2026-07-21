package com.demo.pix.mapper;

import com.demo.pix.dto.PixTransacaoResponse;
import com.demo.pix.entity.PixTransacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PixTransacaoMapper {

    PixTransacaoResponse toResponse(PixTransacao pixTransacao);
}
