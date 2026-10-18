package com.example;

import net.openhft.affinity.AffinityStrategies;
import net.openhft.affinity.AffinityThreadFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AffinityService {
    public static void main(String[] args) throws Exception {
        int nThreads = 4;
        List<Transacao> dados = geraDados.gerar(1_000_000, 42);
        ExecutorService executor = Executors.newFixedThreadPool(nThreads,
                new AffinityThreadFactory("worker", AffinityStrategies.DIFFERENT_CORE));

        try {
            for (int i = 0; i < 3; i++) Paralelo.calcular(dados, executor, nThreads); // warm-up

            long inicio = System.nanoTime();
            Estatistica e = Paralelo.calcular(dados, executor, nThreads);
            long ms = (System.nanoTime() - inicio) / 1_000_000;

            System.out.println("======================================");
            System.out.println("     VERSAO EXECUTOR + AFFINITY");
            System.out.println("======================================");
            System.out.println("Soma dos valores: " + e.soma);
            System.out.println("Média dos valores: " + e.media());
            System.out.println("Maior transação: " + e.maior);
            System.out.println("Menor transação: " + e.menor);
            System.out.println("Tempo (ExecutorService + Affinity): " + ms + " ms");
            System.out.println("======================================");
        } finally {
            executor.shutdown();
        }
    }
}