package com.demo.pix.service;

import com.demo.pix.entity.PixTransacao;
import com.demo.pix.entity.StatusPix;
import com.demo.pix.event.TipoMovimento;
import com.demo.pix.repository.PixTransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PixTransacaoService {

    private final PixTransacaoRepository pixTransacaoRepository;

    public PixTransacaoService(PixTransacaoRepository pixTransacaoRepository) {
        this.pixTransacaoRepository = pixTransacaoRepository;
    }

    @Transactional
    public PixTransacao registrar(UUID transacaoId, Long contaId, BigDecimal valor, TipoMovimento tipoMovimento) {
        PixTransacao pixTransacao = new PixTransacao();
        pixTransacao.setTransacaoId(transacaoId);
        pixTransacao.setContaId(contaId);
        pixTransacao.setValor(valor);
        pixTransacao.setTipoMovimento(tipoMovimento);
        pixTransacao.setStatus(StatusPix.ENVIADA);
        pixTransacao.setCriadoEm(LocalDateTime.now());
        pixTransacao.setAtualizadoEm(LocalDateTime.now());
        return pixTransacaoRepository.save(pixTransacao);
    }

    public Optional<PixTransacao> buscarPorTransacaoId(UUID transacaoId) {
        return pixTransacaoRepository.findByTransacaoId(transacaoId);
    }

    /** Retorna vazio se o transacaoId não corresponde a nenhum PIX que este serviço iniciou. */
    @Transactional
    public Optional<PixTransacao> finalizar(UUID transacaoId, boolean sucesso, String motivo) {
        return pixTransacaoRepository.findByTransacaoId(transacaoId).map(pixTransacao -> {
            pixTransacao.setStatus(sucesso ? StatusPix.CONFIRMADA : StatusPix.REJEITADA);
            pixTransacao.setMotivo(motivo);
            pixTransacao.setAtualizadoEm(LocalDateTime.now());
            return pixTransacao;
        });
    }
}
