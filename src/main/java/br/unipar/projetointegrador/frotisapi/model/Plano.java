package br.unipar.projetointegrador.frotisapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double valor;

    // --- NOVO CAMPO ---
    // true = plano visível/disponível
    // false = plano excluído logicamente
    private Boolean ativo = true;

    // Adicione o import: import com.fasterxml.jackson.annotation.JsonIgnore;

    @OneToMany(mappedBy = "plano")
    @JsonIgnore // <--- ADICIONE ISTO (Impede o loop infinito)
    private List<Matricula> matriculas;

    public Plano() {
    }
}