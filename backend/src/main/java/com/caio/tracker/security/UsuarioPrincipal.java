package com.caio.tracker.security;

import com.caio.tracker.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioPrincipal implements UserDetails {

    private final Long usuarioId;
    private final String email;
    private final String senha;

    public UsuarioPrincipal(Long usuarioId, String email, String senha) {
        this.usuarioId = usuarioId;
        this.email = email;
        this.senha = senha;
    }

    public static UsuarioPrincipal de(Usuario usuario) {
        return new UsuarioPrincipal(usuario.getId(), usuario.getEmail(), usuario.getSenha());
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
