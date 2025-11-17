package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.FichaTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FichaTreinoRepository extends JpaRepository<FichaTreino, Long> {

    @Query("SELECT f FROM FichaTreino f " +
            "LEFT JOIN FETCH f.aluno " +
            "LEFT JOIN FETCH f.instrutor " +
            "WHERE f.id = :id")
    Optional<FichaTreino> findFichaComDiasById(@Param("id") Long id);

    // --- NOVO MÉTODO ---
    @Query("SELECT DISTINCT f FROM FichaTreino f " +
            "LEFT JOIN FETCH f.aluno a " +
            "WHERE f.instrutor.id = :instrutorId AND a.ativo = true")
    List<FichaTreino> findAllByInstrutorId(@Param("instrutorId") Long instrutorId);
    // Busca a Ficha e força o carregamento da lista de diasDeTreino (mas não dos itens)

    @Query("SELECT DISTINCT f FROM FichaTreino f " +
            "LEFT JOIN FETCH f.diasDeTreino " +
            "WHERE f.id = :id")
    Optional<FichaTreino> findByIdWithDiasDeTreino(@Param("id") Long id);

    // --- ADICIONE ESTE MÉTODO NOVO ---
    @Query("SELECT DISTINCT f FROM FichaTreino f LEFT JOIN FETCH f.aluno")
    List<FichaTreino> findAllComAlunos();

    // Busca a Ficha e força o carregamento de TUDO: Aluno, Instrutor, Dias, Itens e Exercícios
    @Query("SELECT DISTINCT f FROM FichaTreino f " +
            "LEFT JOIN FETCH f.aluno " +                // <--- ADICIONADO: Garante o aluno
            "LEFT JOIN FETCH f.instrutor " +            // <--- ADICIONADO: Garante o instrutor
            "LEFT JOIN FETCH f.diasDeTreino dt " +
            "LEFT JOIN FETCH dt.itensTreino it " +
            "LEFT JOIN FETCH it.exercicio " +
            "WHERE f.id = :id")
    Optional<FichaTreino> findByIdCompleta(@Param("id") Long id);
}
