package com.caio.tracker.metricas;

import java.math.BigDecimal;
import java.util.List;

public record ResumoCarteiraResponse(
        List<ResumoAtivoResponse> ativos,
        BigDecimal valorTotalInvestido,
        BigDecimal valorTotalAtual,
        BigDecimal ganhoPerdaTotal,
        BigDecimal percentualTotal
) {
}
