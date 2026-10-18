package com.example;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;


public class ExecService {
    public static void main(String[] args) throws Exception{
        int nThreads = 4;
        List<Transacao> dados = geraDados.gerar(1_000_000, 42);
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        
        try {
            for (int i = 0; i < 3; i++) Paralelo.calcular(dados, executor, nThreads);

            long inicio = System.nanoTime();
            Estatistica e = Paralelo.calcular(dados, executor, nThreads);
            long ms = (System.nanoTime() - inicio) / 1_000_000;

            System.out.println("======================================");
            System.out.println("        VERSAO EXECUTORSERVICE");
            System.out.println("======================================");
            System.out.println("Soma dos valores: " + e.soma);
            System.out.println("Média dos valores: " + e.media());
            System.out.println("Maior transação: " + e.maior);
            System.out.println("Menor transação: " + e.menor);
            System.out.println("Tempo (ExecutorService): " + ms + " ms");
            System.out.println("======================================");

        } finally {
            executor.shutdown();
        } 
    }    
}
