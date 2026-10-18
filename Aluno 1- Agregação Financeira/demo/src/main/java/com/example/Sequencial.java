package com.example;

import java.util.List;

public class Sequencial {
    public static void main(String[] args) {
        List<Transacao> dados = geraDados.gerar(1_000_000, 42);

        for (int i = 0; i < 3; i++) Estatistica.de(dados);

        long inicio = System.nanoTime();
        Estatistica e = Estatistica.de(dados);
        long ms = (System.nanoTime() - inicio) / 1_000_000;

        System.out.println("======================================");
        System.out.println("         VERSAO SEQUENCIAL");
        System.out.println("======================================");
        System.out.println("Soma dos valores: " + e.soma);
        System.out.println("Média dos valores: " + e.media());
        System.out.println("Maior transação: " + e.maior);
        System.out.println("Menor transação: " + e.menor);
        System.out.println("Tempo (sequencial): " + ms + " ms");
        System.out.println("======================================");
    }
}