package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
}