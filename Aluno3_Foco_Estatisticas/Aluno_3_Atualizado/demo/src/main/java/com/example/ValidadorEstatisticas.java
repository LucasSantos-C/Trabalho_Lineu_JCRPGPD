package com.example;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Compara as estatísticas calculadas por implementações diferentes. */
public final class ValidadorEstatisticas {
    private ValidadorEstatisticas() { }

    public static boolean equivalentes(
            Map<String, EstatisticaCategoria> referencia,
            Map<String, EstatisticaCategoria> resultado,
            String nomeResultado) {

        boolean iguais = true;
        Set<String> categorias = new TreeSet<>(referencia.keySet());
        categorias.addAll(resultado.keySet());

        for (String categoria : categorias) {
            EstatisticaCategoria esperado = referencia.get(categoria);
            EstatisticaCategoria obtido = resultado.get(categoria);

            if (esperado == null || obtido == null) {
                System.out.printf("[DIFERENÇA] %s: categoria '%s' não encontrada nas duas versões.%n",
                        nomeResultado, categoria);
                iguais = false;
                continue;
            }

            if (esperado.getQuantidade() != obtido.getQuantidade()) {
                System.out.printf("[DIFERENÇA] %s / %s: quantidade (%d x %d).%n",
                        nomeResultado, categoria, esperado.getQuantidade(), obtido.getQuantidade());
                iguais = false;
            }
            if (esperado.getValorTotalCentavos() != obtido.getValorTotalCentavos()) {
                System.out.printf("[DIFERENÇA] %s / %s: total em centavos (%d x %d).%n",
                        nomeResultado, categoria,
                        esperado.getValorTotalCentavos(), obtido.getValorTotalCentavos());
                iguais = false;
            }
            if (esperado.getMaiorValorCentavos() != obtido.getMaiorValorCentavos()) {
                System.out.printf("[DIFERENÇA] %s / %s: maior transação em centavos (%d x %d).%n",
                        nomeResultado, categoria,
                        esperado.getMaiorValorCentavos(), obtido.getMaiorValorCentavos());
                iguais = false;
            }
            // A média é derivada do mesmo total exato e da mesma quantidade.
            if (Math.abs(esperado.getValorMedio() - obtido.getValorMedio()) > 1e-9) {
                System.out.printf("[DIFERENÇA] %s / %s: média (%.10f x %.10f).%n",
                        nomeResultado, categoria, esperado.getValorMedio(), obtido.getValorMedio());
                iguais = false;
            }
        }

        if (iguais) {
            System.out.printf("[OK] %s: as estatísticas são iguais às da versão sequencial.%n", nomeResultado);
        } else {
            System.out.printf("[ATENÇÃO] %s: foram encontradas diferenças; confira os detalhes acima.%n", nomeResultado);
        }
        return iguais;
    }
}
