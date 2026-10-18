package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class geraDados {
    public static List<Transacao> gerar (int n, long seed) {
        Random r = new Random(seed);

        String[] categorias = new String[5];
        String[] cidades = new String[50];
        String[] estados = new String[27];
        for (int i = 0; i < categorias.length; i++) categorias[i] = "Categoria " + i;
        for (int i = 0; i < cidades.length; i++) cidades[i] = "Cidade " + i;
        for (int i = 0; i < estados.length; i++) estados[i] = "Estado " + i;

        String data = "2024-01-01";
        List<Transacao> lista = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            lista.add(new Transacao(
                    String.valueOf(i),
                    "Cliente " + i,
                    data,
                    r.nextDouble() * 1000,
                    categorias[i % categorias.length],
                    cidades[i % cidades.length],
                    estados[i % estados.length]));
        }
        return lista;
    }
}
