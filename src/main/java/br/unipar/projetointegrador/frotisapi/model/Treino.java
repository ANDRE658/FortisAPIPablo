package br.unipar.projetointegrador.frotisapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Aluno> alunos = new HashSet<>();


    // Adicione a lista de ItemTreino
    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemTreino> itensTreino;


}