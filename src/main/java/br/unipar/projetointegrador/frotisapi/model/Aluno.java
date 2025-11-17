package br.unipar.projetointegrador.frotisapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore; // <--- IMPORTE ISTO (Novo)
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Aluno implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    private String senha;
    private String email;
    private Date dataNascimento;
    private String telefone;
    private String sexo;
    private float altura;
    private float peso;
    private Boolean ativo = true;
    private Integer diaVencimento;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date(); // Inicializa com a data atual

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @JsonBackReference("aluno-treino")
    @ManyToOne
    private Treino treino;

    @OneToMany(mappedBy = "aluno", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("aluno-matriculas")
    private List<Matricula> matriculaList;


    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Evita loop infinito no JSON
    private List<FichaTreino> fichasTreino;

    // --- MÉTODOS USERDETAILS ---


    //Calcula a idade do aluno com base na data de nascimento.
    public int getIdade() {
        if (this.dataNascimento == null) {
            return 0;
        }
        LocalDate dataNasc = this.dataNascimento.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return Period.between(dataNasc, LocalDate.now()).getYears();
    }

    /**
     * Calcula o IMC (Índice de Massa Corporal) do aluno.
     * A altura é armazenada em CM e precisa ser convertida para Metros.
     */
    public double getImc() {
        if (this.altura <= 0) {
            return 0.0;
        }
        // Converte altura de CM para Metros (ex: 175cm -> 1.75m)
        double alturaEmMetros = this.altura / 100.0;
        double imcCalc = this.peso / (alturaEmMetros * alturaEmMetros);

        // Arredonda para 2 casas decimais
        return Math.round(imcCalc * 100.0) / 100.0;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.cpf;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}