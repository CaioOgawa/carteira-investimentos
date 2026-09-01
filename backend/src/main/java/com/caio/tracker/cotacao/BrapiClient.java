package com.caio.tracker.cotacao;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public BrapiClient(@Value("${cotacoes.brapi.base-url}") String baseUrl) {
        this(RestClient.builder().baseUrl(baseUrl).build());
    }

    /** Visível ao pacote para permitir injetar um RestClient de teste (ex: com MockRestServiceServer). */
    BrapiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Map<String, BigDecimal> buscarCotacoes(List<String> ativos) {
        if (ativos.isEmpty()) {
            return Map.of();
        }

        String tickers = String.join(",", ativos);
        BrapiResponse resposta = restClient.get()
                .uri("/quote/" + tickers)
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
