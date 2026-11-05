package com.example;

import java.util.Locale;

/**
 * Acumulador das estatísticas de uma categoria.
 * Os valores monetários são armazenados em centavos (long) para evitar
 * diferenças de arredondamento ao combinar resultados de várias tarefas.
 */
public class EstatisticaCategoria {
    private final String categoria;
    private long quantidade;
    private long valorTotalCentavos;
    private long maiorValorCentavos;

    public EstatisticaCategoria(String categoria) {
        this.categoria = categoria;
        this.quantidade = 0L;
        this.valorTotalCentavos = 0L;
        this.maiorValorCentavos = Long.MIN_VALUE;
    }

    public void acumular(double valor) {
        long valorCentavos = Math.round(valor * 100.0);
        quantidade++;
        valorTotalCentavos += valorCentavos;
        if (valorCentavos > maiorValorCentavos) {
            maiorValorCentavos = valorCentavos;
        }
    }

    public void combinar(EstatisticaCategoria outra) {
        if (!categoria.equals(outra.categoria)) {
            throw new IllegalArgumentException("Não é possível combinar categorias diferentes.");
        }
        quantidade += outra.quantidade;
        valorTotalCentavos += outra.valorTotalCentavos;
        if (outra.maiorValorCentavos > maiorValorCentavos) {
            maiorValorCentavos = outra.maiorValorCentavos;
        }
    }

    public String getCategoria() { return categoria; }
    public long getQuantidade() { return quantidade; }
    public long getValorTotalCentavos() { return valorTotalCentavos; }
    public long getMaiorValorCentavos() {
        return quantidade == 0 ? 0L : maiorValorCentavos;
    }
    public double getValorTotal() { return valorTotalCentavos / 100.0; }
    public double getMaiorValor() { return getMaiorValorCentavos() / 100.0; }
    public double getValorMedio() {
        return quantidade == 0 ? 0.0 : valorTotalCentavos / (100.0 * quantidade);
    }

    @Override
    public String toString() {
        return String.format(
            Locale.forLanguageTag("pt-BR"),
            "| Categoria: %-12s | Quantidade: %,10d | Total: R$ %,14.2f | Média: R$ %,10.2f | Maior: R$ %,10.2f |",
            categoria, quantidade, getValorTotal(), getValorMedio(), getMaiorValor()
        );
    }
}
