package com.caio.tracker.posicao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PosicaoResponse(
        Long id,
        String ativo,
        BigDecimal quantidade,
        BigDecimal precoCompra,
        LocalDate dataCompra
) {

    public static PosicaoResponse from(Posicao posicao) {
        return new PosicaoResponse(
                posicao.getId(),
                posicao.getAtivo(),
                posicao.getQuantidade(),
                posicao.getPrecoCompra(),
                posicao.getDataCompra()
        );
    }
}
