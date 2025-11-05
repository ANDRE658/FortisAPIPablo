package br.unipar.projetointegrador.frotisapi.dto;

import br.unipar.projetointegrador.frotisapi.model.Exercicio;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExercicioDTO {

    private Long id;
    private String nome;
    private int series;
    private int repeticoes;

    // Construtor vazio
    public ExercicioDTO() {
    }

    // Construtor que converte a Entidade (Model) para o DTO
    public ExercicioDTO(Exercicio exercicio) {
        this.id = exercicio.getId();
        this.nome = exercicio.getNome();
        this.series = exercicio.getSeries();
        this.repeticoes = exercicio.getRepeticoes();
    }

    /**
     * NOVO MÉTODO (Melhor Prática):
     * Converte este DTO (dados que vêm do cliente) para uma Entidade
     * que pode ser salva no banco.
     */
    public Exercicio toEntity() {
        Exercicio entidade = new Exercicio();
        entidade.setId(this.id);
        entidade.setNome(this.nome);
        entidade.setSeries(this.series);
        entidade.setRepeticoes(this.repeticoes);
        // O 'treino' será vinculado lá no Service
        return entidade;
    }
}
