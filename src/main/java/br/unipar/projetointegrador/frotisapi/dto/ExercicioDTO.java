package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Exercicio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExercicioDTO {

    private Long id;
    private String nome;

    // Construtor vazio
    public ExercicioDTO() {
    }

    // Construtor que converte a Entidade (Model) para o DTO
    public ExercicioDTO(Exercicio exercicio) {
        this.id = exercicio.getId();
        this.nome = exercicio.getNome();

    }

    /**
     * Converte este DTO para uma Entidade
     */
    public Exercicio toEntity() {
        Exercicio entidade = new Exercicio();
        entidade.setId(this.id);
        entidade.setNome(this.nome);

        return entidade;
    }
}