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
    void buscarCotacoes_comMultiplosAtivos_naoCodificaAVirgulaNaUrl() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://brapi.dev/api");
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();

        // A URL precisa chegar com a vírgula literal (PETR4,VALE3), não %2C -
        // a brapi.dev responde 401 genérico para uma URL malformada dessa forma.
        servidor.expect(requestTo("https://brapi.dev/api/quote/PETR4,VALE3"))
                .andRespond(withSuccess("""
                        {"results":[
                            {"symbol":"PETR4","regularMarketPrice":46.87},
                            {"symbol":"VALE3","regularMarketPrice":78.30}
                        ]}""", MediaType.APPLICATION_JSON));

        BrapiClient client = new BrapiClient(builder.build());
        Map<String, BigDecimal> cotacoes = client.buscarCotacoes(List.of("PETR4", "VALE3"));

        assertThat(cotacoes).containsEntry("PETR4", new BigDecimal("46.87"));
        assertThat(cotacoes).containsEntry("VALE3", new BigDecimal("78.30"));
        servidor.verify();
    }
}
