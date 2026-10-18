package com.example;

import java.util.ArrayList;
import java.util.List;

class Estatistica {
    double soma = 0;
    double maior = Double.NEGATIVE_INFINITY;

    double menor = Double.POSITIVE_INFINITY;
    int quantidade = 0;
    final List<String> idsSuspeitos = new ArrayList<>();
 
    static Estatistica de(List<Transacao> parte) {
        Estatistica e = new Estatistica();
        for (Transacao t : parte) {
            double v = t.getValor();
            e.soma += v;
            if (v > e.maior) e.maior = v;
            if (v < e.menor) e.menor = v;
            e.quantidade++;
            if (ChecarSuspeito.checarSuspeito(t)) {
                e.idsSuspeitos.add(t.getId());
            }
        }
        return e;
    }
 
    void merge(Estatistica outra) {
        soma += outra.soma;
        maior = Math.max(maior, outra.maior);
        menor = Math.min(menor, outra.menor);
        quantidade += outra.quantidade;
        idsSuspeitos.addAll(outra.idsSuspeitos);
    }
 
    double media() { 
        return quantidade == 0 ? 0 : soma / quantidade; 
    }

}
