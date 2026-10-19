package com.example;

import java.util.List;
import java.util.Map;

public class MainAluno03 {

    public static void main(String[] args) throws Exception {
        int totalTransacoes = 10_000_000; 
        long seed = 42L;                   
        int numThreads = 4;              

        // Chama a classe e método do Aluno 02
        System.out.printf("Gerando %,d transações via geraDados...\n", totalTransacoes);
        List<Transacao> transacoes = geraDados.gerar(totalTransacoes, seed);

        System.out.println("\n=======================================================");
        System.out.println("   ALUNO 03: ESTATÍSTICAS POR CATEGORIA DE TRANSAÇÃO");
        System.out.println("=======================================================\n");

        // 1. Processamento Sequencial
        long inicioSeq = System.currentTimeMillis();
        Map<String, EstatisticaCategoria> resSeq = EstatisticaCategoriaSequencial.processar(transacoes);
        long tempoSeq = System.currentTimeMillis() - inicioSeq;

        // 2. Processamento Paralelo (ExecutorService)
        long inicioExec = System.currentTimeMillis();
        Map<String, EstatisticaCategoria> resExec = EstatisticaCategoriaExecutor.processar(transacoes, numThreads);
        long tempoExec = System.currentTimeMillis() - inicioExec;

        // 3. Processamento Paralelo (OpenHFT Affinity)
        long inicioAff = System.currentTimeMillis();
        Map<String, EstatisticaCategoria> resAff = EstatisticaCategoriaAffinity.processar(transacoes, numThreads);
        long tempoAff = System.currentTimeMillis() - inicioAff;

        // Exibição dos resultados acumulados por categoria
        System.out.println("=== RESULTADOS CALCULADOS POR CATEGORIA ===");
        resExec.values().forEach(System.out::println);

        // Indicadores de Desempenho
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
}