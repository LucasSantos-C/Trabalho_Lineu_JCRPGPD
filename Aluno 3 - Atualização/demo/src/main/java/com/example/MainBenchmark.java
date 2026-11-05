package com.example;

import java.util.Map;

public class MainBenchmark {

    public static void main(String[] args) throws Exception {
        int[] transacoesCounts = {1_000_000, 5_000_000, 10_000_000};
        int[] threadCounts = {2, 4, 8};
        long seed = 42L;

        System.out.println("=======================================================");
        System.out.println("          BENCHMARK DE PROCESSAMENTO DE TRANSAÇÕES");
        System.out.println("=======================================================\n");

        for (int totalTransacoes : transacoesCounts) {
            System.out.printf("Gerando %,d transações...\n", totalTransacoes);
            long inicioGeracao = System.currentTimeMillis();
            Transacao[] transacoes = geraDados.gerar(totalTransacoes, seed).toArray(new Transacao[0]);
            System.out.printf("Transações geradas em %d ms.\n\n", System.currentTimeMillis() - inicioGeracao);

            System.out.printf("--- EXECUTANDO PARA %,d TRANSAÇÕES ---\n", totalTransacoes);
            
            long inicioSeq = System.currentTimeMillis();
            EstatisticaCategoriaSequencial.processar(transacoes);
            long tempoSeq = System.currentTimeMillis() - inicioSeq;
            System.out.printf("Tempo Sequencial: %d ms\n", tempoSeq);

            for (int numThreads : threadCounts) {
                long inicioExec = System.currentTimeMillis();
                EstatisticaCategoriaExecutor.processar(transacoes, numThreads);
                long tempoExec = System.currentTimeMillis() - inicioExec;

                long inicioAff = System.currentTimeMillis();
                EstatisticaCategoriaAffinity.processar(transacoes, numThreads);
                long tempoAff = System.currentTimeMillis() - inicioAff;

                double speedupExec = (double) tempoSeq / tempoExec;
                double eficienciaExec = (speedupExec / numThreads) * 100.0;

                double speedupAff = (double) tempoSeq / tempoAff;
                double eficienciaAff = (speedupAff / numThreads) * 100.0;

                System.out.printf("  [Threads: %d] Executor: %d ms (Speedup: %.2fx, Eficiência: %.1f%%) | Affinity: %d ms (Speedup: %.2fx, Eficiência: %.1f%%)\n",
                        numThreads, tempoExec, speedupExec, eficienciaExec, tempoAff, speedupAff, eficienciaAff);
            }
            System.out.println();
        }
        System.out.println("=======================================================");
        System.out.println("                BENCHMARK CONCLUÍDO");
        System.out.println("=======================================================");
    }
}