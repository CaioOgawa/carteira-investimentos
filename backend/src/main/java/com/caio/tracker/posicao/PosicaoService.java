package com.caio.tracker.posicao;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PosicaoService {

    private final PosicaoRepository posicaoRepository;

    public PosicaoService(PosicaoRepository posicaoRepository) {
        this.posicaoRepository = posicaoRepository;
    }

    public List<Posicao> listarTodas() {
        return posicaoRepository.findAll();
    }

    public Posicao buscarPorId(Long id) {
        return posicaoRepository.findById(id)
                .orElseThrow(() -> new PosicaoNotFoundException(id));
    }

    public Posicao criar(PosicaoRequest request) {
        Posicao posicao = new Posicao(
                request.ativo(),
                request.quantidade(),
                request.precoCompra(),
                request.dataCompra()
        );
        return posicaoRepository.save(posicao);
    }

    public Posicao atualizar(Long id, PosicaoRequest request) {
        Posicao posicao = buscarPorId(id);
        posicao.setAtivo(request.ativo());
        posicao.setQuantidade(request.quantidade());
        posicao.setPrecoCompra(request.precoCompra());
        posicao.setDataCompra(request.dataCompra());
        return posicaoRepository.save(posicao);
    }

    public void remover(Long id) {
        Posicao posicao = buscarPorId(id);
        posicaoRepository.delete(posicao);
    }
}
