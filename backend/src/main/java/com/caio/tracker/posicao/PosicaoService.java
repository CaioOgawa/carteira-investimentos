package com.caio.tracker.posicao;

import com.caio.tracker.usuario.Usuario;
import com.caio.tracker.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PosicaoService {

    private final PosicaoRepository posicaoRepository;
    private final UsuarioRepository usuarioRepository;

    public PosicaoService(PosicaoRepository posicaoRepository, UsuarioRepository usuarioRepository) {
        this.posicaoRepository = posicaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Posicao> listarTodas(Long usuarioId) {
        return posicaoRepository.findAllByUsuarioId(usuarioId);
    }

    public Posicao buscarPorId(Long id, Long usuarioId) {
        return posicaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new PosicaoNotFoundException(id));
    }

    public Posicao criar(PosicaoRequest request, Long usuarioId) {
        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);
        Posicao posicao = new Posicao(
                request.ativo(),
                request.quantidade(),
                request.precoCompra(),
                request.dataCompra(),
                usuario
        );
        return posicaoRepository.save(posicao);
    }

    public Posicao atualizar(Long id, PosicaoRequest request, Long usuarioId) {
        Posicao posicao = buscarPorId(id, usuarioId);
        posicao.setAtivo(request.ativo());
        posicao.setQuantidade(request.quantidade());
        posicao.setPrecoCompra(request.precoCompra());
        posicao.setDataCompra(request.dataCompra());
        return posicaoRepository.save(posicao);
    }

    public void remover(Long id, Long usuarioId) {
        Posicao posicao = buscarPorId(id, usuarioId);
        posicaoRepository.delete(posicao);
    }
}
