package com.example;

import java.util.Map;
import java.util.Random;

public class MainAluno03 {

    private static final String[] CATEGORIAS = {
        "Alimentação", "Transporte", "Eletrônicos", "Saúde", "Educação"
    };

    public static Transacao[] gerarTransacoesSinteticas(int quantidade) {
        System.out.printf("Gerando %,d transações sintéticas...\n", quantidade);
        Transacao[] transacoes = new Transacao[quantidade];
        Random random = new Random(42);

        for (int i = 0; i < quantidade; i++) {
            String cat = CATEGORIAS[random.nextInt(CATEGORIAS.length)];
            double valor = 10.0 + (10000.0 - 10.0) * random.nextDouble();
            transacoes[i] = new Transacao(i + 1, "Cliente_" + (i % 50000), valor, cat);
        }
        return transacoes;
    }

    public static void main(String[] args) throws Exception {
        int totalTransacoes = 10_000_000; 
        int numThreads = 4;              

        Transacao[] transacoes = gerarTransacoesSinteticas(totalTransacoes);

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

        System.out.println("=== RESULTADOS CALCULADOS (VERSÃO PARALELA) ===");
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
}