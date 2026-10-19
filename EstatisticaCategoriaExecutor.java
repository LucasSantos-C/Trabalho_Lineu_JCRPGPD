package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class EstatisticaCategoriaExecutor {

    private static class TarefaProcessamentoSlice implements Callable<Map<String, EstatisticaCategoria>> {
        private final Transacao[] transacoes;
        private final int inicio;
        private final int fim;

        public TarefaProcessamentoSlice(Transacao[] transacoes, int inicio, int fim) {
            this.transacoes = transacoes;
            this.inicio = inicio;
            this.fim = fim;
        }

        @Override
        public Map<String, EstatisticaCategoria> call() {
            Map<String, EstatisticaCategoria> mapaParcial = new HashMap<>();
            for (int i = inicio; i < fim; i++) {
                Transacao t = transacoes[i];
                EstatisticaCategoria est = mapaParcial.computeIfAbsent(
                    t.getCategoria(), 
                    k -> new EstatisticaCategoria(k)
                );
                est.acumular(t.getValor());
            }
            return mapaParcial;
        }
    }

    public static Map<String, EstatisticaCategoria> processar(Transacao[] transacoes, int numThreads) 
            throws InterruptedException, ExecutionException {
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Map<String, EstatisticaCategoria>>> futures = new ArrayList<>();

        int tamanho = transacoes.length;
        int tamanhoBloco = tamanho / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int inicio = i * tamanhoBloco;
            int fim = (i == numThreads - 1) ? tamanho : (i + 1) * tamanhoBloco;
            futures.add(executor.submit(new TarefaProcessamentoSlice(transacoes, inicio, fim)));
        }

        Map<String, EstatisticaCategoria> mapaFinal = new HashMap<>();
        for (Future<Map<String, EstatisticaCategoria>> future : futures) {
            Map<String, EstatisticaCategoria> mapaParcial = future.get();
            for (Map.Entry<String, EstatisticaCategoria> entry : mapaParcial.entrySet()) {
                mapaFinal.computeIfAbsent(
                    entry.getKey(), 
                    k -> new EstatisticaCategoria(k)
                ).combinar(entry.getValue());
            }
        }

        executor.shutdown();
        return mapaFinal;
    }
}