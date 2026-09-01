package com.caio.tracker.posicao;

import com.caio.tracker.usuario.Usuario;
import com.caio.tracker.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PosicaoServiceTest {

    @Mock
    private PosicaoRepository posicaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PosicaoService posicaoService;

    private static final Long USUARIO_ID = 1L;

    private PosicaoRequest requestPetr4() {
        return new PosicaoRequest("PETR4", BigDecimal.TEN, new BigDecimal("32.50"), LocalDate.of(2026, 1, 15));
    }

    @Test
    void listarTodas_devolveApenasPosicoesDoUsuario() {
        Posicao posicao = new Posicao("PETR4", BigDecimal.TEN, new BigDecimal("32.50"), LocalDate.now(), mock(Usuario.class));
        when(posicaoRepository.findAllByUsuarioId(USUARIO_ID)).thenReturn(List.of(posicao));

        List<Posicao> resultado = posicaoService.listarTodas(USUARIO_ID);

        assertThat(resultado).containsExactly(posicao);
    }

    @Test
    void buscarPorId_quandoNaoEncontrada_lancaExcecao() {
        when(posicaoRepository.findByIdAndUsuarioId(99L, USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> posicaoService.buscarPorId(99L, USUARIO_ID))
                .isInstanceOf(PosicaoNotFoundException.class);
    }

    @Test
    void criar_associaPosicaoAoUsuarioAutenticado() {
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.getReferenceById(USUARIO_ID)).thenReturn(usuario);
        when(posicaoRepository.save(any(Posicao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Posicao criada = posicaoService.criar(requestPetr4(), USUARIO_ID);

        assertThat(criada.getAtivo()).isEqualTo("PETR4");
        assertThat(criada.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void atualizar_alteraCamposDaPosicaoExistente() {
        Posicao existente = new Posicao("PETR4", BigDecimal.TEN, new BigDecimal("32.50"), LocalDate.now(), mock(Usuario.class));
        when(posicaoRepository.findByIdAndUsuarioId(1L, USUARIO_ID)).thenReturn(Optional.of(existente));
        when(posicaoRepository.save(any(Posicao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PosicaoRequest novoRequest = new PosicaoRequest("PETR4", new BigDecimal("20"), new BigDecimal("35.00"), LocalDate.of(2026, 2, 1));
        Posicao atualizada = posicaoService.atualizar(1L, novoRequest, USUARIO_ID);

        assertThat(atualizada.getQuantidade()).isEqualByComparingTo("20");
        assertThat(atualizada.getPrecoCompra()).isEqualByComparingTo("35.00");
    }

    @Test
    void remover_quandoPertenceAoUsuario_deletaPosicao() {
        Posicao existente = new Posicao("PETR4", BigDecimal.TEN, new BigDecimal("32.50"), LocalDate.now(), mock(Usuario.class));
        when(posicaoRepository.findByIdAndUsuarioId(1L, USUARIO_ID)).thenReturn(Optional.of(existente));

        posicaoService.remover(1L, USUARIO_ID);

        verify(posicaoRepository, times(1)).delete(existente);
    }

    @Test
    void remover_quandoDeOutroUsuario_lancaNotFoundSemDeletar() {
        when(posicaoRepository.findByIdAndUsuarioId(eq(1L), eq(2L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> posicaoService.remover(1L, 2L))
                .isInstanceOf(PosicaoNotFoundException.class);
    }
}
