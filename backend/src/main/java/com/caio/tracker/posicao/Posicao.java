package com.caio.tracker.posicao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Posicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ativo;
    private BigDecimal quantidade;
    private BigDecimal precoCompra;
    private LocalDate dataCompra;

    protected Posicao() {
    }

    public Posicao(String ativo, BigDecimal quantidade, BigDecimal precoCompra, LocalDate dataCompra) {
        this.ativo = ativo;
        this.quantidade = quantidade;
        this.precoCompra = precoCompra;
        this.dataCompra = dataCompra;
    }

    public Long getId() {
        return id;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }
}
