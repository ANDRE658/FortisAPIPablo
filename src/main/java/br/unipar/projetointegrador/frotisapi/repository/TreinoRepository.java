package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; // Importação necessária
import java.util.Optional;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {

    /**
     * Esta consulta agora funciona (busca 1 List e 1 Set).
     * Ela corrige o erro 404 ao ADICIONAR e DELETAR exercícios.
     */
    @Query("SELECT DISTINCT t FROM Treino t " +
            "LEFT JOIN FETCH t.exercicios " +
            "LEFT JOIN FETCH t.alunos " +
            "WHERE t.id = :id")
    Optional<Treino> findTreinoCompletoById(@Param("id") Long id);

    /**
     * Esta consulta também funciona agora.
     * Ela corrige o "Falha ao buscar treino de hoje".
     */
    @Query("SELECT t FROM Treino t LEFT JOIN FETCH t.exercicios LEFT JOIN FETCH t.alunos WHERE t.diaSemana = :diaSemana")
    Optional<Treino> findTreinoCompletoByDiaSemana(@Param("diaSemana") String diaSemana);

    /**
     * Esta consulta também funciona agora.
     * Ela corrige o "Falha ao buscar a lista de treinos" (na tela Home).
     */
    @Query("SELECT DISTINCT t FROM Treino t LEFT JOIN FETCH t.exercicios LEFT JOIN FETCH t.alunos")
    List<Treino> findAllTreinosCompletos();
}