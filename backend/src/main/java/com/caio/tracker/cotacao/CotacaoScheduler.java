package com.caio.tracker.cotacao;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CotacaoScheduler {

    private final CotacaoService cotacaoService;

    public CotacaoScheduler(CotacaoService cotacaoService) {
        this.cotacaoService = cotacaoService;
    }

    @Scheduled(cron = "${cotacoes.atualizacao.cron}")
    public void atualizar() {
        cotacaoService.atualizarCotacoes();
    }
}
