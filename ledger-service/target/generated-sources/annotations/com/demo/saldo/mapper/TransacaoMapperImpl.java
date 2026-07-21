package com.demo.saldo.mapper;

import com.demo.saldo.dto.TransacaoResponse;
import com.demo.saldo.entity.TipoMovimento;
import com.demo.saldo.entity.TipoTransacao;
import com.demo.saldo.entity.Transacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-18T18:00:08+0000",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class TransacaoMapperImpl implements TransacaoMapper {

    @Override
    public TransacaoResponse toResponse(Transacao transacao) {
        if ( transacao == null ) {
            return null;
        }

        String correlationId = null;
        BigDecimal valor = null;
        TipoMovimento tipoMovimento = null;
        TipoTransacao tipoTransacao = null;
        LocalDateTime criadoEm = null;

        correlationId = transacao.getCorrelationId();
        valor = transacao.getValor();
        tipoMovimento = transacao.getTipoMovimento();
        tipoTransacao = transacao.getTipoTransacao();
        criadoEm = transacao.getCriadoEm();

        TransacaoResponse transacaoResponse = new TransacaoResponse( correlationId, valor, tipoMovimento, tipoTransacao, criadoEm );

        return transacaoResponse;
    }
}
