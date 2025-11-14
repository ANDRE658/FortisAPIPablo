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
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @JsonBackReference("aluno-matriculas") // Nome correspondente// Evita o loop no JSON
    @ManyToOne
    @JoinColumn(name = "aluno_id") // Cria a coluna da chave estrangeira no banco
    private Aluno aluno;

    // Removemos o @JsonBackReference para o plano aparecer no JSON
    @ManyToOne
    @JoinColumn(name = "plano_id")
    private Plano plano;

    // --- NOVO CAMPO ---
    @ManyToOne
    @JoinColumn(name = "instrutor_id")
    private Instrutor instrutor;
}
