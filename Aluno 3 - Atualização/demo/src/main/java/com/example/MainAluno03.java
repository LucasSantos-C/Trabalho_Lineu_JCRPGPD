package com.example;

import java.util.List;
import java.util.Map;

public class MainAluno03 {

    public static void main(String[] args) throws Exception {
        int totalTransacoes = 20_200_000; 
        long seed = 42L;                   
        int numThreads = 4;              

        System.out.printf("Gerando %,d transações via geraDados...\n", totalTransacoes);
        Transacao[] transacoes = geraDados.gerar(totalTransacoes, seed);

        System.out.println("\n=======================================================");
        System.out.println("   ALUNO 03: ESTATÍSTICAS POR CATEGORIA DE TRANSAÇÃO");
        System.out.println("=======================================================\n");

        long inicioSeq = System.currentTimeMillis();
        Map<String, EstatisticaCategoria> resSeq = EstatisticaCategoriaSequencial.processar(transacoes);
        long tempoSeq = System.currentTimeMillis() - inicioSeq;

        long inicioExec = System.currentTimeMillis();
        Map<String, EstatisticaCategoria> resExec = EstatisticaCategoriaExecutor.processar(transacoes, numThreads);
        long tempoExec = System.currentTimeMillis() - inicioExec;

        long inicioAff = System.currentTimeMillis();
        Map<String, EstatisticaCategoria> resAff = EstatisticaCategoriaAffinity.processar(transacoes, numThreads);
        long tempoAff = System.currentTimeMillis() - inicioAff;

        System.out.println("\n=== CONFERÊNCIA ENTRE IMPLEMENTAÇÕES ===");

        boolean executorOK =
            compararResultados("ExecutorService", resSeq, resExec);

        boolean affinityOK =
            compararResultados("ExecutorService + Affinity", resSeq, resAff);

        long totalContabilizado = somarQuantidades(resSeq);

        boolean quantidadeOK = totalContabilizado == transacoes.length;

        System.out.println("\nTransações contabilizadas: "
                + totalContabilizado + " de " + transacoes.length);

        System.out.println("Conferência geral: "
                + (executorOK && affinityOK && quantidadeOK
                    ? "APROVADA"
                    : "REPROVADA"));

        System.out.println("=== RESULTADOS CALCULADOS POR CATEGORIA ===");
        resExec.values().forEach(System.out::println);

        double speedupExec = (double) tempoSeq / tempoExec;
        double eficienciaExec = (speedupExec / numThreads) * 100.0;

        double speedupAff = (double) tempoSeq / tempoAff;
        double eficienciaAff = (speedupAff / numThreads) * 100.0;

        System.out.println("\n=== DESEMPENHO E BENCHMARK (ALUNO 03) ===");
        System.out.printf("Tempo Sequencial        : %d ms\n", tempoSeq);
        System.out.printf("Tempo Executor (%d th)  : %d ms | Speedup: %.2fx | Eficiência: %.1f%%\n",
                numThreads, tempoExec, speedupExec, eficienciaExec);
        System.out.printf("Tempo Affinity (%d th)  : %d ms | Speedup: %.2fx | Eficiência: %.1f%%\n",
                numThreads, tempoAff, speedupAff, eficienciaAff);
    }
        private static boolean compararResultados(
            String nome,
            Map<String, EstatisticaCategoria> referencia,
            Map<String, EstatisticaCategoria> resultado) {

        boolean iguais = true;

        // Verifica se as duas implementações possuem
        // exatamente as mesmas categorias.
        if (!referencia.keySet().equals(resultado.keySet())) {
            System.out.println("[ERRO] Categorias diferentes em " + nome);
            return false;
        }

        for (String categoria : referencia.keySet()) {

            EstatisticaCategoria a = referencia.get(categoria);
            EstatisticaCategoria b = resultado.get(categoria);

            // Quantidade deve ser exatamente igual.
            boolean quantidadeOK =
                    a.getQuantidade() == b.getQuantidade();

            // Valores financeiros são comparados em centavos,
            // evitando diferenças insignificantes de double.
            boolean totalOK =
                    Math.round(a.getValorTotal() * 100)
                    == Math.round(b.getValorTotal() * 100);

            boolean mediaOK =
                    Math.round(a.getValorMedio() * 100)
                    == Math.round(b.getValorMedio() * 100);

            boolean maiorOK =
                    Math.round(a.getMaiorValor() * 100)
                    == Math.round(b.getMaiorValor() * 100);

            if (!quantidadeOK || !totalOK || !mediaOK || !maiorOK) {

                iguais = false;

                System.out.println(
                    "[DIVERGÊNCIA] Categoria: " + categoria
                );

                System.out.println(
                    "Quantidade: " + a.getQuantidade()
                    + " / " + b.getQuantidade()
                );

                System.out.println(
                    "Total: " + a.getValorTotal()
                    + " / " + b.getValorTotal()
                );

                System.out.println(
                    "Média: " + a.getValorMedio()
                    + " / " + b.getValorMedio()
                );

                System.out.println(
                    "Maior: " + a.getMaiorValor()
                    + " / " + b.getMaiorValor()
                );
            }
        }

        System.out.println(
            (iguais ? "[OK] " : "[ERRO] ")
            + nome + ": "
            + (iguais
                ? "todas as estatísticas são iguais à versão sequencial."
                : "foram encontradas diferenças.")
        );

        return iguais;
    }


    private static long somarQuantidades(
            Map<String, EstatisticaCategoria> mapa) {

        long total = 0;

        for (EstatisticaCategoria estatistica : mapa.values()) {
            total += estatistica.getQuantidade();
        }

        return total;
    }
}
