package com.caio.tracker.posicao;

public class PosicaoNotFoundException extends RuntimeException {

    public PosicaoNotFoundException(Long id) {
        super("Posição não encontrada: " + id);
    }
}
