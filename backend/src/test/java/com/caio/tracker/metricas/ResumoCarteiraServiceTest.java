package com.caio.tracker.metricas;

import com.caio.tracker.cotacao.Cotacao;
import com.caio.tracker.cotacao.CotacaoService;
import com.caio.tracker.posicao.Posicao;
import com.caio.tracker.posicao.PosicaoService;
import com.caio.tracker.usuario.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumoCarteiraServiceTest {

    @Mock
    private PosicaoService posicaoService;

    @Mock
    private CotacaoService cotacaoService;

    @InjectMocks
    private ResumoCarteiraService resumoCarteiraService;

    private static final Long USUARIO_ID = 1L;

    @Test
    void calcular_semCotacaoDisponivel_usaPrecoDeCompraComoAtual() {
        Posicao posicao = new Posicao("PETR4", BigDecimal.TEN, new BigDecimal("30.00"), LocalDate.now(), mock(Usuario.class));
        when(posicaoService.listarTodas(USUARIO_ID)).thenReturn(List.of(posicao));
        when(cotacaoService.cotacaoAtual("PETR4")).thenReturn(Optional.empty());

        ResumoCarteiraResponse resumo = resumoCarteiraService.calcular(USUARIO_ID);

        assertThat(resumo.valorTotalInvestido()).isEqualByComparingTo("300.00");
        assertThat(resumo.valorTotalAtual()).isEqualByComparingTo("300.00");
        assertThat(resumo.ganhoPerdaTotal()).isEqualByComparingTo("0.00");
        assertThat(resumo.percentualTotal()).isEqualByComparingTo("0.00");
    }

    @Test
    void calcular_comCotacaoMaisAlta_calculaGanhoEPercentualCorretos() {
        Posicao posicao = new Posicao("PETR4", BigDecimal.TEN, new BigDecimal("30.00"), LocalDate.now(), mock(Usuario.class));
        when(posicaoService.listarTodas(USUARIO_ID)).thenReturn(List.of(posicao));
        when(cotacaoService.cotacaoAtual("PETR4"))
                .thenReturn(Optional.of(new Cotacao("PETR4", new BigDecimal("33.00"), Instant.now())));

        ResumoCarteiraResponse resumo = resumoCarteiraService.calcular(USUARIO_ID);

        // investido = 10 * 30 = 300; atual = 10 * 33 = 330; ganho = 30; % = 10.00
        assertThat(resumo.valorTotalInvestido()).isEqualByComparingTo("300.00");
        assertThat(resumo.valorTotalAtual()).isEqualByComparingTo("330.00");
        assertThat(resumo.ganhoPerdaTotal()).isEqualByComparingTo("30.00");
        assertThat(resumo.percentualTotal()).isEqualByComparingTo("10.00");
        assertThat(resumo.ativos()).hasSize(1);
        assertThat(resumo.ativos().get(0).percentual()).isEqualByComparingTo("10.00");
    }

    @Test
    void calcular_semPosicoes_devolveTotaisZerados() {
        when(posicaoService.listarTodas(USUARIO_ID)).thenReturn(List.of());

        ResumoCarteiraResponse resumo = resumoCarteiraService.calcular(USUARIO_ID);

        assertThat(resumo.ativos()).isEmpty();
        assertThat(resumo.valorTotalInvestido()).isEqualByComparingTo("0.00");
        assertThat(resumo.percentualTotal()).isEqualByComparingTo("0.00");
    }
}
