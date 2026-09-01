package com.caio.tracker.usuario;

public record AuthResponse(String token, String nome, String email) {
}
