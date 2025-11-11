// CRIE ESTE NOVO ARQUIVO:
// src/main/java/br/unipar/projetointegrador/frotisapi/model/FichaTreino.java

package br.unipar.projetointegrador.frotisapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class FichaTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    // --- ADICIONE A ANOTAÇÃO AQUI ---
    @JsonManagedReference("ficha-dias") // Novo "apelido"
    @OneToMany(mappedBy = "fichaTreino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Treino> diasDeTreino;

    @JsonBackReference("instrutor-fichas") // O MESMO "apelido"
    @ManyToOne
    @JoinColumn(name = "instrutor_id")
    private Instrutor instrutor;
}