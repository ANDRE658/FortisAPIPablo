package br.unipar.projetointegrador.frotisapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter // <-- ESSENCIAL PARA GERAR O getId()
@Setter // <-- ESSENCIAL PARA GERAR OS SETTERS
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double valor;

    // Se você tiver um relacionamento com Matrícula, por exemplo:
    @OneToMany(mappedBy = "plano")
    private List<Matricula> matriculas;

    // Construtores, se necessário...
    public Plano() {
    }
}