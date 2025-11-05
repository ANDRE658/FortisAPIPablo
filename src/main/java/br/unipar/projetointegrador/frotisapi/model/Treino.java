package br.unipar.projetointegrador.frotisapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet; // <-- MUDANÇA (importar Set)
import java.util.List;
import java.util.Set; // <-- MUDANÇA (importar Set)

@Entity
@Getter
@Setter
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String diaSemana;

    @ManyToOne
    @JoinColumn(name = "instrutor_id")
    @JsonIgnore
    private Instrutor instrutor;

    // <-- MUDANÇA: "List<Aluno>" virou "Set<Aluno>"
    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Aluno> alunos = new HashSet<>(); // <-- MUDANÇA

    // A lista de exercícios pode continuar sendo uma List
    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exercicio> exercicios;
}