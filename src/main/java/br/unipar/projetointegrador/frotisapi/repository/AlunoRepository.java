package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByCpf(String cpf);
    // 👇 **** ADICIONE ESTE MÉTODO ****
    @Query("SELECT DISTINCT a FROM Aluno a " +
            "LEFT JOIN FETCH a.matriculaList m " +
            "LEFT JOIN FETCH m.plano")
    List<Aluno> findAllWithMatriculasAndPlanos();

}
