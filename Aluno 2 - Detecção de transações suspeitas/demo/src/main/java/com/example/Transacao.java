package com.example;

public class Transacao {
    private final String id;
    private final String cliente;
    private final String data;
    private final double valor;
    private final String categoria;
    private final String cidade;
    private final String estado;

    Transacao(String id, String cliente, String data, double valor,
              String categoria, String cidade, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.data = data;
        this.valor = valor;
        this.categoria = categoria;
        this.cidade = cidade;
        this.estado = estado;
    }

    public double getValor() {
        return valor;
    }

    public String getId() {
        return id;
    }

    public void imprimeTransacao() {
        System.out.println("ID: " + id);
        System.out.println("Cliente: " + cliente);
        System.out.println("Data: " + data);
        System.out.println("Valor: " + valor);
        System.out.println("Categoria: " + categoria);
        System.out.println("Cidade: " + cidade);
        System.out.println("Estado: " + estado);
    }
}
