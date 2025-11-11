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

    // (Mude o nome de findFichaCompletaById para findFichaComDiasById)
    @Query("SELECT f FROM FichaTreino f " +
            "LEFT JOIN FETCH f.aluno " +
            "LEFT JOIN FETCH f.instrutor " +
            "LEFT JOIN FETCH f.diasDeTreino d " +
            // "LEFT JOIN FETCH d.itensTreino i " + // <-- REMOVA/COMENTE ESTA LINHA
            // "LEFT JOIN FETCH i.exercicio " +     // <-- REMOVA/COMENTE ESTA LINHA
            "WHERE f.id = :id")
    Optional<FichaTreino> findFichaComDiasById(@Param("id") Long id); // <-- MUDE O NOME DO MÉTODO AQUI

    // --- ADICIONE ESTE MÉTODO NOVO ---
    @Query("SELECT DISTINCT f FROM FichaTreino f LEFT JOIN FETCH f.aluno")
    List<FichaTreino> findAllComAlunos();
}
