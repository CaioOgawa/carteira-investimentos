package com.caio.tracker.usuario;

import com.caio.tracker.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrar_comEmailNovo_criaUsuarioEDevolveToken() {
        RegistroRequest request = new RegistroRequest("Caio", "caio@teste.com", "senha123");

        when(usuarioRepository.existsByEmail("caio@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash");
        when(jwtService.gerarToken(any(), any())).thenReturn("token-fake");

        AuthResponse resposta = authService.registrar(request);

        assertThat(resposta.token()).isEqualTo("token-fake");
        assertThat(resposta.email()).isEqualTo("caio@teste.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrar_comEmailJaExistente_lancaExcecaoSemSalvar() {
        RegistroRequest request = new RegistroRequest("Caio", "caio@teste.com", "senha123");
        when(usuarioRepository.existsByEmail("caio@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void login_comCredenciaisValidas_devolveToken() {
        LoginRequest request = new LoginRequest("caio@teste.com", "senha123");
        Usuario usuario = new Usuario("Caio", "caio@teste.com", "hash");

        when(usuarioRepository.findByEmail("caio@teste.com")).thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken(any(), any())).thenReturn("token-fake");

        AuthResponse resposta = authService.login(request);

        assertThat(resposta.token()).isEqualTo("token-fake");
        verify(authenticationManager).authenticate(any());
    }
}
