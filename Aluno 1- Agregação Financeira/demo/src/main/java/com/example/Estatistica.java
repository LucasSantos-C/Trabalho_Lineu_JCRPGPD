package com.example;

import java.util.List;

class Estatistica {
    double soma = 0;
    double maior = Double.NEGATIVE_INFINITY;

    double menor = Double.POSITIVE_INFINITY;
    int quantidade = 0;
 
    static Estatistica de(List<Transacao> parte) {
        Estatistica e = new Estatistica();
        for (Transacao t : parte) {
            double v = t.getValor();
            e.soma += v;
            if (v > e.maior) e.maior = v;
            if (v < e.menor) e.menor = v;
            e.quantidade++;
        }
        return e;
    }
 
    void merge(Estatistica outra) {
        soma += outra.soma;
        maior = Math.max(maior, outra.maior);
        menor = Math.min(menor, outra.menor);
        quantidade += outra.quantidade;
    }
 
    double media() { 
        return quantidade == 0 ? 0 : soma / quantidade; 
    }

}
