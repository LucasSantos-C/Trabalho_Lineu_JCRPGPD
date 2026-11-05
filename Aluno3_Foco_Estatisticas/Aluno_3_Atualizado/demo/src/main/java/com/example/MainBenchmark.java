package com.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;

public class MainBenchmark {

    private static final String CABECALHO_CSV =
        "transacoes,threads,sequencial_ms,executor_ms,executor_speedup,executor_eficiencia_pct," +
        "affinity_ms,affinity_speedup,affinity_eficiencia_pct,executor_validacao,affinity_validacao\n";

    public static void main(String[] args) throws Exception {
        // Volumes mantidos dentro do limite que já foi executado no computador.
        int[] transacoesCounts = {1_000_000, 5_000_000, 10_000_000};
        int[] threadCounts = {1, 2, 4, 8};
        long seed = 42L;

        Path pastaProjeto = localizarPastaProjeto();
        Path arquivoCsv = args.length > 0
                ? Path.of(args[0]).toAbsolutePath()
                : pastaProjeto.resolve("resultados_benchmark.csv");

        if (arquivoCsv.getParent() != null) {
            Files.createDirectories(arquivoCsv.getParent());
        }
        Files.writeString(arquivoCsv, CABECALHO_CSV, StandardCharsets.UTF_8);

        System.out.println("=======================================================");
        System.out.println("          BENCHMARK DE PROCESSAMENTO DE TRANSAÇÕES");
        System.out.println("=======================================================\n");

        for (int totalTransacoes : transacoesCounts) {
            executarBenchmarkDoVolume(totalTransacoes, seed, threadCounts, arquivoCsv);
        }

        System.out.println("=======================================================");
        System.out.println("                BENCHMARK CONCLUÍDO");
        System.out.println("CSV gerado em: " + arquivoCsv);
        System.out.println("Execute: python gerar_graficos.py");
        System.out.println("=======================================================");
    }

    private static void executarBenchmarkDoVolume(
            int totalTransacoes,
            long seed,
            int[] threadCounts,
            Path arquivoCsv) throws Exception {

        System.out.printf("Gerando %,d transações...%n", totalTransacoes);
        long inicioGeracao = System.nanoTime();
        Transacao[] transacoes = geraDados.gerar(totalTransacoes, seed).toArray(new Transacao[0]);
        double tempoGeracao = nanosParaMs(System.nanoTime() - inicioGeracao);
        System.out.printf(Locale.US, "Transações geradas em %.2f ms.%n%n", tempoGeracao);

        System.out.printf("--- EXECUTANDO PARA %,d TRANSAÇÕES --- %n", totalTransacoes);

        long inicioSeq = System.nanoTime();
        Map<String, EstatisticaCategoria> resultadoSequencial =
                EstatisticaCategoriaSequencial.processar(transacoes);
        double tempoSeq = nanosParaMs(System.nanoTime() - inicioSeq);
        System.out.printf(Locale.US, "Tempo Sequencial: %.2f ms%n", tempoSeq);

        for (int numThreads : threadCounts) {
            long inicioExec = System.nanoTime();
            Map<String, EstatisticaCategoria> resultadoExecutor =
                    EstatisticaCategoriaExecutor.processar(transacoes, numThreads);
            double tempoExec = nanosParaMs(System.nanoTime() - inicioExec);

            long inicioAff = System.nanoTime();
            Map<String, EstatisticaCategoria> resultadoAffinity =
                    EstatisticaCategoriaAffinity.processar(transacoes, numThreads);
            double tempoAff = nanosParaMs(System.nanoTime() - inicioAff);

            boolean executorOk = ValidadorEstatisticas.equivalentes(
                    resultadoSequencial, resultadoExecutor, "ExecutorService (" + numThreads + " threads)"
            );
            boolean affinityOk = ValidadorEstatisticas.equivalentes(
                    resultadoSequencial, resultadoAffinity, "Affinity (" + numThreads + " threads)"
            );

            double speedupExec = tempoExec > 0 ? tempoSeq / tempoExec : 0;
            double eficienciaExec = (speedupExec / numThreads) * 100.0;
            double speedupAff = tempoAff > 0 ? tempoSeq / tempoAff : 0;
            double eficienciaAff = (speedupAff / numThreads) * 100.0;

            System.out.printf(Locale.US,
                "[Threads: %d] Executor: %.2f ms (Speedup: %.2fx, Eficiência: %.1f%%) | " +
                "Affinity: %.2f ms (Speedup: %.2fx, Eficiência: %.1f%%)%n",
                numThreads, tempoExec, speedupExec, eficienciaExec,
                tempoAff, speedupAff, eficienciaAff
            );
            System.out.printf("  Conferência: Executor %s | Affinity %s%n%n",
                    executorOk ? "OK" : "FALHOU",
                    affinityOk ? "OK" : "FALHOU");

            salvarLinhaCsv(arquivoCsv, totalTransacoes, numThreads, tempoSeq,
                    tempoExec, speedupExec, eficienciaExec,
                    tempoAff, speedupAff, eficienciaAff, executorOk, affinityOk);
        }

        // A referência às transações deixa de ser necessária após este volume.
        System.out.println();
    }

    private static void salvarLinhaCsv(
            Path arquivoCsv,
            int transacoes,
            int threads,
            double sequencial,
            double executor,
            double speedupExecutor,
            double eficienciaExecutor,
            double affinity,
            double speedupAffinity,
            double eficienciaAffinity,
            boolean executorOk,
            boolean affinityOk) throws IOException {

        String linha = String.format(Locale.US,
            "%d,%d,%.4f,%.4f,%.6f,%.4f,%.4f,%.6f,%.4f,%s,%s%n",
            transacoes, threads, sequencial, executor, speedupExecutor, eficienciaExecutor,
            affinity, speedupAffinity, eficienciaAffinity, executorOk, affinityOk
        );
        Files.writeString(arquivoCsv, linha, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static Path localizarPastaProjeto() {
        Path atual = Path.of("").toAbsolutePath();
        if (Files.exists(atual.resolve("pom.xml"))) {
            return atual;
        }
        if (Files.exists(atual.resolve("demo/pom.xml"))) {
            return atual.resolve("demo");
        }
        return atual;
    }

    private static double nanosParaMs(long nanos) {
        return nanos / 1_000_000.0;
    }
}
