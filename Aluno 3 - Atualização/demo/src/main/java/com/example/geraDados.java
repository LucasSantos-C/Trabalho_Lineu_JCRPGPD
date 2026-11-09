package com.example;

import java.util.Random;
import java.time.LocalDate;

class geraDados {

    public static Transacao[] gerar(int n, long seed) {

        Random r = new Random(seed);

        String[] cidades = new String[50];
        String[] estados = new String[27];

        String[] categorias = {
            "Alimentação",
            "Transporte",
            "Saúde",
            "Eletrônicos",
            "Educação"
        };

        for (int i = 0; i < cidades.length; i++) {
            cidades[i] = "Cidade " + i;
        }

        for (int i = 0; i < estados.length; i++) {
            estados[i] = "Estado " + i;
        }

        LocalDate dataInicial = LocalDate.of(2020, 1, 1);
        LocalDate dataFinal = LocalDate.of(2025, 12, 31);

        int quantidadeDias = (int) (
            dataFinal.toEpochDay() -
            dataInicial.toEpochDay() + 1
        );

        // Criação direta do array, sem ArrayList intermediária
        Transacao[] transacoes = new Transacao[n];

        for (int i = 0; i < n; i++) {

            int centavos = r.nextInt(
                10_000_000 - 100 + 1
            ) + 100;

            transacoes[i] = new Transacao(
                String.valueOf(i),
                "Cliente " + r.nextInt(10_000),
                dataInicial.plusDays(
                    r.nextInt(quantidadeDias)
                ).toString(),
                centavos / 100.0,
                categorias[r.nextInt(categorias.length)],
                cidades[r.nextInt(cidades.length)],
                estados[r.nextInt(estados.length)]
            );
        }

        return transacoes;
    }
}
