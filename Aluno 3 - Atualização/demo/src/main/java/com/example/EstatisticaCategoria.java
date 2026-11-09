package com.example;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    // Acumula uma transação utilizando centavos inteiros.
    public void acumular(double valor) {
        long centavos = Math.round(valor * 100.0);

        quantidade++;
        valorTotalCentavos += centavos;

        if (centavos > maiorValorCentavos) {
            maiorValorCentavos = centavos;
        }
    }

    // Combina os resultados de duas tarefas.
    public void combinar(EstatisticaCategoria outra) {
        if (outra == null || outra.quantidade == 0) {
            return;
        }

        if (quantidade == 0 ||
                outra.maiorValorCentavos > maiorValorCentavos) {
            maiorValorCentavos = outra.maiorValorCentavos;
        }

        quantidade += outra.quantidade;
        valorTotalCentavos += outra.valorTotalCentavos;
    }

    public String getCategoria() {
        return categoria;
    }

    public long getQuantidade() {
        return quantidade;
    }

    public double getValorTotal() {
        return valorTotalCentavos / 100.0;
    }

    public long getValorTotalCentavos() {
        return valorTotalCentavos;
    }

    public double getMaiorValor() {
        return quantidade == 0
                ? 0.0
                : maiorValorCentavos / 100.0;
    }

    public long getMaiorValorCentavos() {
        return quantidade == 0
                ? 0L
                : maiorValorCentavos;
    }

    public double getValorMedio() {
        return quantidade == 0
                ? 0.0
                : valorTotalCentavos / 100.0 / quantidade;
    }

    public long getValorMedioCentavosArredondado() {
        return quantidade == 0
                ? 0L
                : Math.round((double) valorTotalCentavos / quantidade);
    }

    @Override
    public String toString() {

        BigDecimal total =
                BigDecimal.valueOf(valorTotalCentavos, 2);

        BigDecimal media = quantidade == 0
                ? BigDecimal.ZERO.setScale(2)
                : total.divide(
                        BigDecimal.valueOf(quantidade),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal maior =
                BigDecimal.valueOf(getMaiorValorCentavos(), 2);

        return String.format(
                "| Categoria: %-12s | Qtd: %,10d | Total: R$ %,14.2f | Média: R$ %,10.2f | Maior: R$ %,10.2f |",
                categoria,
                quantidade,
                total,
                media,
                maior
        );
    }
}
