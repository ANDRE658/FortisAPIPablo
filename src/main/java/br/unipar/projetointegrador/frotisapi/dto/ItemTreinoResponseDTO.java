package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.ItemTreino;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemTreinoResponseDTO {

    private Long id;
    private String nomeExercicio;
    private int series;
    private String repeticoes;
    private int carga;
    private int tempoDescansoSegundos;

    // Construtor que converte a Entidade (Model) para o DTO
    public ItemTreinoResponseDTO(ItemTreino item) {
        this.id = item.getId();
        // O 'item' tem um 'Exercicio' dentro dele, que tem o 'nome'
        this.nomeExercicio = item.getExercicio().getNome();
        this.series = item.getSeries();
        this.repeticoes = item.getRepeticoes();
        this.carga = item.getCarga();
        this.tempoDescansoSegundos = item.getTempoDescansoSegundos();
    }
}