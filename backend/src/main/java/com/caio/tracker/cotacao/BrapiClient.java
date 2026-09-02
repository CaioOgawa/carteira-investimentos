package com.caio.tracker.cotacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class BrapiClient {

    private static final Logger log = LoggerFactory.getLogger(BrapiClient.class);

    private final RestClient restClient;
    private final String token;

    @Autowired
    public BrapiClient(
            @Value("${cotacoes.brapi.base-url}") String baseUrl,
            @Value("${cotacoes.brapi.token:}") String token
    ) {
        this(RestClient.builder().baseUrl(baseUrl).build(), token);
    }

    /** Visível ao pacote para permitir injetar um RestClient de teste (ex: com MockRestServiceServer). */
    BrapiClient(RestClient restClient, String token) {
        this.restClient = restClient;
        this.token = token;
    }

    /**
     * O plano gratuito da brapi.dev permite só 1 ativo por requisição (QUOTES_PER_REQUEST_EXCEEDED
     * pra qualquer coisa acima disso), então buscamos um ticker por vez — uma falha isolada
     * (ticker inválido, rate limit pontual) não derruba a atualização dos demais.
     */
    public Map<String, BigDecimal> buscarCotacoes(List<String> ativos) {
        Map<String, BigDecimal> precos = new HashMap<>();
        for (String ativo : ativos) {
            buscarCotacao(ativo).ifPresent(preco -> precos.put(ativo, preco));
        }
        return precos;
    }

    private Optional<BigDecimal> buscarCotacao(String ativo) {
        try {
            BrapiResponse resposta = restClient.get()
                    .uri("/quote/" + ativo)
                    .headers(headers -> {
                        if (token != null && !token.isBlank()) {
                            headers.setBearerAuth(token);
                        }
                    })
                    .retrieve()
                    .body(BrapiResponse.class);

            if (resposta == null || resposta.results() == null) {
                return Optional.empty();
            }

            return resposta.results().stream()
                    .filter(r -> r.symbol().equals(ativo))
                    .map(BrapiResultado::regularMarketPrice)
                    .filter(Objects::nonNull)
                    .findFirst();
        } catch (RestClientException e) {
            log.warn("Falha ao buscar cotação de {}: {}", ativo, e.getMessage());
            return Optional.empty();
        }
    }
}
