package com.caio.tracker.metricas;

import java.math.BigDecimal;

public record ResumoAtivoResponse(
        String ativo,
        BigDecimal quantidade,
        BigDecimal precoCompra,
        BigDecimal precoAtual,
        BigDecimal valorInvestido,
        BigDecimal valorAtual,
        BigDecimal ganhoPerda,
        BigDecimal percentual
) {
}
