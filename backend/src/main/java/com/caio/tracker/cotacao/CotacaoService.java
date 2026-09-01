package com.caio.tracker.cotacao;

import com.caio.tracker.posicao.Posicao;
import com.caio.tracker.posicao.PosicaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class CotacaoService {

    private static final Logger log = LoggerFactory.getLogger(CotacaoService.class);

    private final PosicaoRepository posicaoRepository;
    private final CotacaoRepository cotacaoRepository;
    private final BrapiClient brapiClient;

    public CotacaoService(PosicaoRepository posicaoRepository, CotacaoRepository cotacaoRepository, BrapiClient brapiClient) {
        this.posicaoRepository = posicaoRepository;
        this.cotacaoRepository = cotacaoRepository;
        this.brapiClient = brapiClient;
    }

    public void atualizarCotacoes() {
        var ativos = posicaoRepository.findAll().stream()
                .map(Posicao::getAtivo)
                .distinct()
                .toList();

        if (ativos.isEmpty()) {
            return;
        }

        try {
            Map<String, BigDecimal> precos = brapiClient.buscarCotacoes(ativos);
            Instant agora = Instant.now();
            precos.forEach((ativo, preco) -> cotacaoRepository.save(new Cotacao(ativo, preco, agora)));
            log.info("Cotações atualizadas para {} ativo(s)", precos.size());
        } catch (RestClientException e) {
            log.warn("Falha ao atualizar cotações via brapi.dev: {}", e.getMessage());
        }
    }

    public Optional<Cotacao> cotacaoAtual(String ativo) {
        return cotacaoRepository.findTopByAtivoOrderByTimestampDesc(ativo);
    }
}
