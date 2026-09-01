package com.caio.tracker.posicao;

import java.util.List;

public record ImportacaoResponse(List<PosicaoResponse> importadas, List<ErroImportacao> erros) {
}
