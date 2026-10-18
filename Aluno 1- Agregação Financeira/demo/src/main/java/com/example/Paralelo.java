package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class Paralelo {
    static Estatistica calcular(List<Transacao> dados, ExecutorService ex, int partes)
            throws Exception {

        int tam = (int) Math.ceil(dados.size() / (double) partes);
        List<Future<Estatistica>> futuros = new ArrayList<>();
 
        for (int i = 0; i < dados.size(); i += tam) {
            final List<Transacao> parte = dados.subList(i, Math.min(i + tam, dados.size()));
            futuros.add(ex.submit(() -> Estatistica.de(parte)));
        }
 
        Estatistica total = new Estatistica();
        for (Future<Estatistica> f : futuros) {
            total.merge(f.get());
        }
        return total;
    }
}