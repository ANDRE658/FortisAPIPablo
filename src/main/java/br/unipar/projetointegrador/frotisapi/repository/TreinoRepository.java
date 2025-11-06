package br.unipar.projetointegrador.frotisapi.repository;

import br.unipar.projetointegrador.frotisapi.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {

    // Estes métodos estão CORRETOS e sendo usados
    @Query("SELECT DISTINCT t FROM Treino t " +
            "LEFT JOIN FETCH t.itensTreino " +
            "LEFT JOIN FETCH t.alunos " +
            "WHERE t.id = :id")
    Optional<Treino> findTreinoCompletoById(@Param("id") Long id);

    @Query("SELECT t FROM Treino t LEFT JOIN FETCH t.itensTreino LEFT JOIN FETCH t.alunos WHERE t.diaSemana = :diaSemana")
    Optional<Treino> findTreinoCompletoByDiaSemana(@Param("diaSemana") String diaSemana);

    @Query("SELECT DISTINCT t FROM Treino t LEFT JOIN FETCH t.itensTreino LEFT JOIN FETCH t.alunos")
    List<Treino> findAllTreinosCompletos();

    // --- INÍCIO DA CORREÇÃO ---
    // Remova ou comente este método. Ele é o que está causando o crash da API.
    /*
    Optional<Treino> findByFichaTreino_IdAndDiaSemana(Long fichaTreinoId, String diaSemana);
    */
    // --- FIM DA CORREÇÃO ---
}