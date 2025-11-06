package br.unipar.projetointegrador.frotisapi.dto;

import lombok.Data;

@Data
public class ItemTreinoRequestDTO {
    // ID do Exercício (do catálogo, ex: 1 = Supino)
    private Long exercicioId;

    private int series;
    private String repeticoes; // "10-12"
    private int carga;
    private int tempoDescansoSegundos;
}