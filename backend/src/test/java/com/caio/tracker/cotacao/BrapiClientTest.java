package com.caio.tracker.cotacao;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BrapiClientTest {

    @Test
    void buscarCotacoes_comMultiplosAtivos_fazUmaRequisicaoPorTicker() {
        // Plano gratuito da brapi.dev permite só 1 ativo por requisição.
        RestClient.Builder builder = RestClient.builder().baseUrl("https://brapi.dev/api");
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();

        servidor.expect(requestTo("https://brapi.dev/api/quote/PETR4"))
                .andRespond(withSuccess("""
                        {"results":[{"symbol":"PETR4","regularMarketPrice":46.87}]}""", MediaType.APPLICATION_JSON));
        servidor.expect(requestTo("https://brapi.dev/api/quote/VALE3"))
                .andRespond(withSuccess("""
                        {"results":[{"symbol":"VALE3","regularMarketPrice":78.30}]}""", MediaType.APPLICATION_JSON));

        BrapiClient client = new BrapiClient(builder.build(), "");
        Map<String, BigDecimal> cotacoes = client.buscarCotacoes(List.of("PETR4", "VALE3"));

        assertThat(cotacoes).containsEntry("PETR4", new BigDecimal("46.87"));
        assertThat(cotacoes).containsEntry("VALE3", new BigDecimal("78.30"));
        servidor.verify();
    }

    @Test
    void buscarCotacoes_quandoUmTickerFalha_mantemOsDemais() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://brapi.dev/api");
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();

        servidor.expect(requestTo("https://brapi.dev/api/quote/PETR4"))
                .andRespond(withSuccess("""
                        {"results":[{"symbol":"PETR4","regularMarketPrice":46.87}]}""", MediaType.APPLICATION_JSON));
        servidor.expect(requestTo("https://brapi.dev/api/quote/TICKERINVALIDO"))
                .andRespond(org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest());

        BrapiClient client = new BrapiClient(builder.build(), "");
        Map<String, BigDecimal> cotacoes = client.buscarCotacoes(List.of("PETR4", "TICKERINVALIDO"));

        assertThat(cotacoes).containsEntry("PETR4", new BigDecimal("46.87"));
        assertThat(cotacoes).doesNotContainKey("TICKERINVALIDO");
        servidor.verify();
    }
}
