package com.caio.tracker.posicao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PosicaoRepository extends JpaRepository<Posicao, Long> {

    List<Posicao> findAllByUsuarioId(Long usuarioId);

    Optional<Posicao> findByIdAndUsuarioId(Long id, Long usuarioId);
}
