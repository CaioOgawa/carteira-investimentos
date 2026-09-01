package com.caio.tracker.cotacao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrapiResponse(List<BrapiResultado> results) {
}
