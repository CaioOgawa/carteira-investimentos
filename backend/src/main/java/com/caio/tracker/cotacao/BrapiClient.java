package com.caio.tracker.cotacao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BrapiClient {

    private final RestClient restClient;

    public BrapiClient(@Value("${cotacoes.brapi.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public Map<String, BigDecimal> buscarCotacoes(List<String> ativos) {
        if (ativos.isEmpty()) {
            return Map.of();
        }

        String tickers = String.join(",", ativos);
        BrapiResponse resposta = restClient.get()
                .uri("/quote/{tickers}", tickers)
                .retrieve()
                .body(BrapiResponse.class);

        if (resposta == null || resposta.results() == null) {
            return Map.of();
        }

        return resposta.results().stream()
                .filter(r -> r.regularMarketPrice() != null)
                .collect(Collectors.toMap(BrapiResultado::symbol, BrapiResultado::regularMarketPrice));
    }
}
