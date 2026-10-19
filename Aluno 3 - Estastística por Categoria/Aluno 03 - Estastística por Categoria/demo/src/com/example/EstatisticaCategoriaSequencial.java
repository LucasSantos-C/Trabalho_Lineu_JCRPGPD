package com.example;

import java.util.HashMap;
import java.util.Map;

public class EstatisticaCategoriaSequencial {

    public static Map<String, EstatisticaCategoria> processar(Transacao[] transacoes) {
        Map<String, EstatisticaCategoria> mapa = new HashMap<>();

        for (Transacao t : transacoes) {
            EstatisticaCategoria est = mapa.computeIfAbsent(
                t.getCategoria(), 
                k -> new EstatisticaCategoria(k)
            );
            est.acumular(t.getValor());
        }

        return mapa;
    }
}