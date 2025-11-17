package br.unipar.projetointegrador.frotisapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login; // Pode ser o email ou um username
    private String senha;

    // Tipo do Usuário Adm, Instrutor, Aluno, etc.
    @Enumerated(EnumType.STRING) // <-- MUDE DE .ORDINAL PARA .STRING
    private Role role;


    // Opcional: Se um Usuário DEVE ser um Instrutor
    @OneToOne
    @JoinColumn(name = "instrutor_id")
    private Instrutor instrutor; // Referência ao seu modelo Instrutor já existente

    // Opcional: Se um Usuário DEVE ser um Aluno
    @OneToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    // --- Métodos do UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Pode adicionar lógica de expiração
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Pode adicionar lógica de bloqueio
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Se o usuário for um Aluno, usa o status 'ativo' do Aluno
        if (this.aluno != null) {
            return Boolean.TRUE.equals(this.aluno.getAtivo());
        }

        // Se o usuário for um Instrutor, usa o status 'ativo' do Instrutor
        if (this.instrutor != null) {
            return Boolean.TRUE.equals(this.instrutor.getAtivo());
        }

        // Para outros tipos de usuário (ou se não tiver vínculo), permite o login
        return true;
    }
}