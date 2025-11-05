package br.unipar.projetointegrador.frotisapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int series;
    private int repeticoes;

    @JsonBackReference("exercicio-treino") //evita loop infinito no JSON
    @ManyToOne //muitos exercícios pertencem a Um treino
    @JoinColumn(name = "treino_id") //chave estrangeira
    private Treino treino;

}
