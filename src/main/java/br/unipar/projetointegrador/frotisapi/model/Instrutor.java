package br.unipar.projetointegrador.frotisapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Instrutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String CPF;
    private Date dataNascimento;
    private String telefone;
    private String email;
    private String sexo;

    // --- NOVO CAMPO ---
    private Boolean ativo = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @JsonManagedReference("instrutor-fichas")
    @OneToMany(mappedBy = "instrutor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // <-- ADICIONE ESTA LINHA PARA QUEBRAR O LOOP
    private List<FichaTreino> fichasSupervisionadas;
}