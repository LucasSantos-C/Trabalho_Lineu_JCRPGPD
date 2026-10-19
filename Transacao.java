package com.example;

public class Transacao {
    private final long id;
    private final String cliente;
    private final double valor;
    private final String categoria;

    public Transacao(long id, String cliente, double valor, String categoria) {
        this.id = id;
        this.cliente = cliente;
        this.valor = valor;
        this.categoria = categoria;
    }

    public long getId() { return id; }
    public String getCliente() { return cliente; }
    public double getValor() { return valor; }
    public String getCategoria() { return categoria; }
}