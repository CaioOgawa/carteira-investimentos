package com.caio.tracker.cotacao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrapiResultado(String symbol, BigDecimal regularMarketPrice) {
}
