package com.caio.tracker.posicao;

import com.caio.tracker.usuario.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PosicaoImportServiceTest {

    private static final Long USUARIO_ID = 1L;

    @Mock
    private PosicaoService posicaoService;

    @InjectMocks
    private PosicaoImportService posicaoImportService;

    private MockMultipartFile arquivo(String conteudo) {
        return new MockMultipartFile("arquivo", "carteira.csv", "text/csv", conteudo.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void importar_comLinhasValidas_criaTodasAsPosicoes() {
        String csv = """
                ativo,quantidade,precoCompra,dataCompra
                PETR4,100,32.50,2026-01-15
                VALE3,50,60.00,2026-02-01
                """;

        when(posicaoService.criar(any(PosicaoRequest.class), eq(USUARIO_ID)))
                .thenAnswer(invocation -> {
                    PosicaoRequest r = invocation.getArgument(0);
                    return new Posicao(r.ativo(), r.quantidade(), r.precoCompra(), r.dataCompra(), mock(Usuario.class));
                });

        ImportacaoResponse resposta = posicaoImportService.importar(arquivo(csv), USUARIO_ID);

        assertThat(resposta.importadas()).hasSize(2);
        assertThat(resposta.erros()).isEmpty();
        assertThat(resposta.importadas().get(0).ativo()).isEqualTo("PETR4");
    }

    @Test
    void importar_comLinhaInvalida_reportaErroSemDerrubarAsDemais() {
        String csv = """
                ativo,quantidade,precoCompra,dataCompra
                PETR4,100,32.50,2026-01-15
                VALE3,-5,60.00,2026-02-01
                ITUB4,10,abc,2026-02-01
                """;

        when(posicaoService.criar(any(PosicaoRequest.class), eq(USUARIO_ID)))
                .thenAnswer(invocation -> {
                    PosicaoRequest r = invocation.getArgument(0);
                    return new Posicao(r.ativo(), r.quantidade(), r.precoCompra(), r.dataCompra(), mock(Usuario.class));
                });

        ImportacaoResponse resposta = posicaoImportService.importar(arquivo(csv), USUARIO_ID);

        assertThat(resposta.importadas()).hasSize(1);
        assertThat(resposta.erros()).hasSize(2);
        assertThat(resposta.erros().get(0).linha()).isEqualTo(3);
        assertThat(resposta.erros().get(1).linha()).isEqualTo(4);
    }

    @Test
    void importar_comCabecalhoErrado_lancaExcecao() {
        String csv = "ticker,qtd,preco,data\nPETR4,100,32.50,2026-01-15\n";

        assertThatThrownBy(() -> posicaoImportService.importar(arquivo(csv), USUARIO_ID))
                .isInstanceOf(ArquivoImportacaoInvalidoException.class);
    }

    @Test
    void importar_comArquivoVazio_lancaExcecao() {
        assertThatThrownBy(() -> posicaoImportService.importar(arquivo(""), USUARIO_ID))
                .isInstanceOf(ArquivoImportacaoInvalidoException.class);
    }
}
