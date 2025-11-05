package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByCpf(String cpf);

}
