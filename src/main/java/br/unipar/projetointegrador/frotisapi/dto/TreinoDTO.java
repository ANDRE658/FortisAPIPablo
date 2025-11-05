package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Treino;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class TreinoDTO {
    private Long id;
    private String nome;
    private String diaSemana;
    private List<ExercicioDTO> exercicios;

    // Construtor que converte a Entidade para DTO
    public TreinoDTO(Treino treino) {
        this.id = treino.getId();
        this.nome = treino.getNome();
        this.diaSemana = treino.getDiaSemana();
        // Converte a lista de Entidades Exercicio para uma lista de ExercicioDTO
        this.exercicios = treino.getExercicios().stream()
                .map(ExercicioDTO::new)
                .collect(Collectors.toList());
    }
}