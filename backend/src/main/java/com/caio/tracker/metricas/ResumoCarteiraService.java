package com.caio.tracker.metricas;

import com.caio.tracker.cotacao.Cotacao;
import com.caio.tracker.cotacao.CotacaoService;
import com.caio.tracker.posicao.Posicao;
import com.caio.tracker.posicao.PosicaoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ResumoCarteiraService {

    private final PosicaoService posicaoService;
    private final CotacaoService cotacaoService;

    public ResumoCarteiraService(PosicaoService posicaoService, CotacaoService cotacaoService) {
        this.posicaoService = posicaoService;
        this.cotacaoService = cotacaoService;
    }

    public ResumoCarteiraResponse calcular(Long usuarioId) {
        List<ResumoAtivoResponse> resumosPorAtivo = posicaoService.listarTodas(usuarioId).stream()
                .map(this::resumoDe)
                .toList();

        BigDecimal valorTotalInvestido = somar(resumosPorAtivo, ResumoAtivoResponse::valorInvestido);
        BigDecimal valorTotalAtual = somar(resumosPorAtivo, ResumoAtivoResponse::valorAtual);
        BigDecimal ganhoPerdaTotal = valorTotalAtual.subtract(valorTotalInvestido);
        BigDecimal percentualTotal = percentual(ganhoPerdaTotal, valorTotalInvestido);

        return new ResumoCarteiraResponse(
                resumosPorAtivo,
                valorTotalInvestido,
                valorTotalAtual,
                ganhoPerdaTotal,
                percentualTotal
        );
    }

    private ResumoAtivoResponse resumoDe(Posicao posicao) {
        BigDecimal precoAtual = cotacaoService.cotacaoAtual(posicao.getAtivo())
                .map(Cotacao::getPreco)
                .orElse(posicao.getPrecoCompra());

        BigDecimal valorInvestido = posicao.getQuantidade().multiply(posicao.getPrecoCompra());
        BigDecimal valorAtual = posicao.getQuantidade().multiply(precoAtual);
        BigDecimal ganhoPerda = valorAtual.subtract(valorInvestido);

        return new ResumoAtivoResponse(
                posicao.getAtivo(),
                posicao.getQuantidade(),
                posicao.getPrecoCompra(),
                precoAtual,
                valorInvestido.setScale(2, RoundingMode.HALF_UP),
                valorAtual.setScale(2, RoundingMode.HALF_UP),
                ganhoPerda.setScale(2, RoundingMode.HALF_UP),
                percentual(ganhoPerda, valorInvestido)
        );
    }

    private BigDecimal percentual(BigDecimal ganhoPerda, BigDecimal valorInvestido) {
        if (valorInvestido.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return ganhoPerda
                .divide(valorInvestido, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal somar(List<ResumoAtivoResponse> resumos, java.util.function.Function<ResumoAtivoResponse, BigDecimal> extrator) {
        return resumos.stream()
                .map(extrator)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
