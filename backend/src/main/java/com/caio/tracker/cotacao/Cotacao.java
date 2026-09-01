package com.caio.tracker.cotacao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Cotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ativo;
    private BigDecimal preco;
    private Instant timestamp;

    protected Cotacao() {
    }

    public Cotacao(String ativo, BigDecimal preco, Instant timestamp) {
        this.ativo = ativo;
        this.preco = preco;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getAtivo() {
        return ativo;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
