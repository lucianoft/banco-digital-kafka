package com.demo.pix.mapper;

import com.demo.pix.dto.PixTransacaoResponse;
import com.demo.pix.entity.PixTransacao;
import com.demo.pix.entity.StatusPix;
import com.demo.pix.event.TipoMovimento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-18T18:01:15+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PixTransacaoMapperImpl implements PixTransacaoMapper {

    @Override
    public PixTransacaoResponse toResponse(PixTransacao pixTransacao) {
        if ( pixTransacao == null ) {
            return null;
        }

        UUID transacaoId = null;
        Long contaId = null;
        BigDecimal valor = null;
        TipoMovimento tipoMovimento = null;
        StatusPix status = null;
        String motivo = null;
        LocalDateTime criadoEm = null;
        LocalDateTime atualizadoEm = null;

        transacaoId = pixTransacao.getTransacaoId();
        contaId = pixTransacao.getContaId();
        valor = pixTransacao.getValor();
        tipoMovimento = pixTransacao.getTipoMovimento();
        status = pixTransacao.getStatus();
        motivo = pixTransacao.getMotivo();
        criadoEm = pixTransacao.getCriadoEm();
        atualizadoEm = pixTransacao.getAtualizadoEm();

        PixTransacaoResponse pixTransacaoResponse = new PixTransacaoResponse( transacaoId, contaId, valor, tipoMovimento, status, motivo, criadoEm, atualizadoEm );

        return pixTransacaoResponse;
    }
}
