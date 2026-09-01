package com.caio.tracker.posicao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PosicaoRequest(

        @NotBlank(message = "ativo é obrigatório")
        String ativo,

        @NotNull(message = "quantidade é obrigatória")
        @Positive(message = "quantidade deve ser maior que zero")
        BigDecimal quantidade,

        @NotNull(message = "precoCompra é obrigatório")
        @PositiveOrZero(message = "precoCompra não pode ser negativo")
        BigDecimal precoCompra,

        @NotNull(message = "dataCompra é obrigatória")
        LocalDate dataCompra
) {
}
