package com.example;

public class EstatisticaCategoria {
    private String categoria;
    private long quantidade;
    private double valorTotal;
    private double maiorValor;

    public EstatisticaCategoria(String categoria) {
        this.categoria = categoria;
        this.quantidade = 0;
        this.valorTotal = 0.0;
        this.maiorValor = Double.MIN_VALUE;
    }

    public void acumular(double valor) {
        this.quantidade++;
        this.valorTotal += valor;
        if (valor > this.maiorValor) {
            this.maiorValor = valor;
        }
    }

    public void combinar(EstatisticaCategoria outra) {
        this.quantidade += outra.quantidade;
        this.valorTotal += outra.valorTotal;
        if (outra.maiorValor > this.maiorValor) {
            this.maiorValor = outra.maiorValor;
        }
    }

    public String getCategoria() { return categoria; }
    public long getQuantidade() { return quantidade; }
    public double getValorTotal() { return valorTotal; }
    public double getMaiorValor() { return maiorValor == Double.MIN_VALUE ? 0.0 : maiorValor; }
    public double getValorMedio() {
        return quantidade == 0 ? 0.0 : valorTotal / quantidade;
    }

    @Override
    public String toString() {
        return String.format(
            "| Categoria: %-12s | Qtd: %,10d | Total: R$ %,14.2f | Média: R$ %,10.2f | Maior: R$ %,10.2f |",
            categoria, quantidade, valorTotal, getValorMedio(), getMaiorValor()
        );
    }
}