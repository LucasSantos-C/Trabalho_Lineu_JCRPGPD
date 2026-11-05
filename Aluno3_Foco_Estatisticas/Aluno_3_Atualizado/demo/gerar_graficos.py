#!/usr/bin/env python3
"""Gera gráficos comparativos a partir do CSV produzido pelo MainBenchmark."""

import csv
import sys
from pathlib import Path

import matplotlib.pyplot as plt


BASE_DIR = Path(__file__).resolve().parent
CSV_PADRAO = BASE_DIR / "resultados_benchmark.csv"
PASTA_GRAFICOS = BASE_DIR / "graficos"


def ler_resultados(caminho):
    if not caminho.exists():
        raise FileNotFoundError(
            f"Arquivo não encontrado: {caminho}\n"
            "Execute MainBenchmark.java primeiro para gerar o CSV."
        )

    with caminho.open("r", newline="", encoding="utf-8") as arquivo:
        linhas = list(csv.DictReader(arquivo))

    if not linhas:
        raise ValueError("O CSV não possui resultados. Execute o MainBenchmark novamente.")

    return linhas


def ordenar_volumes(linhas):
    return sorted({int(linha["transacoes"]) for linha in linhas})


def rotulo_volume(valor):
    if valor >= 1_000_000:
        return f"{valor / 1_000_000:g} mi"
    return f"{valor:,}".replace(",", ".")


def salvar_grafico_tempos(linhas, volumes):
    plt.figure(figsize=(11, 6))

    sequenciais = {}
    for linha in linhas:
        volume = int(linha["transacoes"])
        sequenciais[volume] = float(linha["sequencial_ms"])

    plt.plot(
        [rotulo_volume(v) for v in volumes],
        [sequenciais[v] for v in volumes],
        marker="o", linewidth=2.5, label="Sequencial"
    )

    for campo, nome in (("executor_ms", "ExecutorService"), ("affinity_ms", "ExecutorService + Affinity")):
        threads_disponiveis = sorted({int(l["threads"]) for l in linhas})
        for threads in threads_disponiveis:
            dados = {
                int(l["transacoes"]): float(l[campo])
                for l in linhas if int(l["threads"]) == threads
            }
            valores = [dados.get(v) for v in volumes]
            if all(valor is not None for valor in valores):
                plt.plot(
                    [rotulo_volume(v) for v in volumes], valores,
                    marker="o", label=f"{nome} ({threads} thread(s))"
                )

    plt.title("Comparação do tempo de processamento")
    plt.xlabel("Quantidade de transações")
    plt.ylabel("Tempo (ms) — menor é melhor")
    plt.grid(True, alpha=0.3)
    plt.legend(fontsize=8, ncol=2)
    plt.tight_layout()
    destino = PASTA_GRAFICOS / "comparacao_tempos.png"
    plt.savefig(destino, dpi=160)
    plt.close()
    return destino


def salvar_grafico_metrica(linhas, volumes, campo_executor, campo_affinity, titulo, eixo_y, nome_arquivo):
    plt.figure(figsize=(11, 6))
    threads_disponiveis = sorted({int(l["threads"]) for l in linhas})

    for campo, nome in ((campo_executor, "ExecutorService"), (campo_affinity, "ExecutorService + Affinity")):
        for threads in threads_disponiveis:
            dados = {
                int(l["transacoes"]): float(l[campo])
                for l in linhas if int(l["threads"]) == threads
            }
            valores = [dados.get(v) for v in volumes]
            if all(valor is not None for valor in valores):
                plt.plot(
                    [rotulo_volume(v) for v in volumes], valores,
                    marker="o", label=f"{nome} ({threads} thread(s))"
                )

    plt.title(titulo)
    plt.xlabel("Quantidade de transações")
    plt.ylabel(eixo_y)
    plt.grid(True, alpha=0.3)
    plt.legend(fontsize=8, ncol=2)
    plt.tight_layout()
    destino = PASTA_GRAFICOS / nome_arquivo
    plt.savefig(destino, dpi=160)
    plt.close()
    return destino


def main():
    caminho_csv = Path(sys.argv[1]).expanduser() if len(sys.argv) > 1 else CSV_PADRAO
    if not caminho_csv.is_absolute():
        caminho_csv = (Path.cwd() / caminho_csv).resolve()

    linhas = ler_resultados(caminho_csv)
    volumes = ordenar_volumes(linhas)
    PASTA_GRAFICOS.mkdir(parents=True, exist_ok=True)

    falhas = [
        linha for linha in linhas
        if linha["executor_validacao"].lower() != "true"
        or linha["affinity_validacao"].lower() != "true"
    ]
    if falhas:
        print("ATENÇÃO: o CSV contém configurações cuja validação estatística falhou.")
        print("Confira as mensagens do MainBenchmark antes de utilizar os gráficos no relatório.")

    arquivos = [
        salvar_grafico_tempos(linhas, volumes),
        salvar_grafico_metrica(
            linhas, volumes, "executor_speedup", "affinity_speedup",
            "Comparação de Speedup", "Speedup (vezes)", "comparacao_speedup.png"
        ),
        salvar_grafico_metrica(
            linhas, volumes, "executor_eficiencia_pct", "affinity_eficiencia_pct",
            "Comparação de eficiência paralela", "Eficiência (%)", "comparacao_eficiencia.png"
        ),
    ]

    print("\nGráficos gerados com sucesso:")
    for arquivo in arquivos:
        print(f"- {arquivo}")
    print(f"\nDados utilizados: {caminho_csv}")


if __name__ == "__main__":
    main()
