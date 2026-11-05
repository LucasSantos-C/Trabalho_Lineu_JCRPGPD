package com.example;

import java.util.Map;
import java.util.TreeMap;
import java.util.Locale;

/** Programa de demonstração da contribuição do Aluno 3: estatísticas por categoria. */
public class MainAluno03 {

    public static void main(String[] args) throws Exception {
        // O padrão de 10 milhões corresponde ao maior volume já executado no computador.
        // É possível informar outro volume como primeiro argumento e threads como segundo.
        int totalTransacoes = args.length > 0 ? Integer.parseInt(args[0]) : 10_000_000;
        int numThreads = args.length > 1 ? Integer.parseInt(args[1]) : 4;
        long seed = 42L;

        if (totalTransacoes <= 0 || numThreads <= 0) {
            throw new IllegalArgumentException("A quantidade de transações e de threads deve ser positiva.");
        }

        System.out.printf(Locale.forLanguageTag("pt-BR"),
                "Gerando %,d transações...%n", totalTransacoes);
        Transacao[] transacoes = geraDados.gerar(totalTransacoes, seed).toArray(new Transacao[0]);

        System.out.println("\n===============================================================");
        System.out.println("       ALUNO 3 — ESTATÍSTICAS DE TRANSAÇÕES POR CATEGORIA");
        System.out.println("===============================================================\n");

        Map<String, EstatisticaCategoria> resultadoSequencial =
                EstatisticaCategoriaSequencial.processar(transacoes);
        Map<String, EstatisticaCategoria> resultadoExecutor =
                EstatisticaCategoriaExecutor.processar(transacoes, numThreads);
        Map<String, EstatisticaCategoria> resultadoAffinity =
                EstatisticaCategoriaAffinity.processar(transacoes, numThreads);

        System.out.println("CONFERÊNCIA ENTRE AS IMPLEMENTAÇÕES");
        boolean executorOk = ValidadorEstatisticas.equivalentes(
                resultadoSequencial, resultadoExecutor, "ExecutorService");
        boolean affinityOk = ValidadorEstatisticas.equivalentes(
                resultadoSequencial, resultadoAffinity, "ExecutorService + Affinity");

        // Exibe uma única tabela estatística. Os três mapas já foram comparados acima.
        System.out.println("\nESTATÍSTICAS POR CATEGORIA");
        System.out.printf("%-16s %15s %20s %20s %20s%n",
                "Categoria", "Quantidade", "Valor total", "Valor médio", "Maior transação");
        System.out.println("-".repeat(96));

        Map<String, EstatisticaCategoria> ordenado = new TreeMap<>(resultadoExecutor);
        for (EstatisticaCategoria estatistica : ordenado.values()) {
            System.out.printf(Locale.forLanguageTag("pt-BR"),
                    "%-16s %,15d R$ %,17.2f R$ %,17.2f R$ %,17.2f%n",
                    estatistica.getCategoria(),
                    estatistica.getQuantidade(),
                    estatistica.getValorTotal(),
                    estatistica.getValorMedio(),
                    estatistica.getMaiorValor());
        }

        long quantidadeContabilizada = resultadoExecutor.values().stream()
                .mapToLong(EstatisticaCategoria::getQuantidade).sum();
        System.out.println("-".repeat(96));
        System.out.printf(Locale.forLanguageTag("pt-BR"),
                "Transações contabilizadas: %,d de %,d%n",
                quantidadeContabilizada, totalTransacoes);
        System.out.println("Conferência geral: " +
                (executorOk && affinityOk && quantidadeContabilizada == totalTransacoes
                        ? "APROVADA" : "REVISAR RESULTADOS"));
    }
}
