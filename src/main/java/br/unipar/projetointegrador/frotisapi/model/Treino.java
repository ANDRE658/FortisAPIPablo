package br.unipar.projetointegrador.frotisapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    private String diaSemana; // "SEGUNDA", "TERCA", etc. (Isso fica)

    private String nome;

    @JsonBackReference("ficha-dias") // O MESMO "apelido"
    @ManyToOne
    @JoinColumn(name = "ficha_treino_id")
    private FichaTreino fichaTreino;


    @JsonManagedReference("treino-itens")
    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemTreino> itensTreino;

}