package com.caio.tracker.cotacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CotacaoRepository extends JpaRepository<Cotacao, Long> {

    Optional<Cotacao> findTopByAtivoOrderByTimestampDesc(String ativo);
}
