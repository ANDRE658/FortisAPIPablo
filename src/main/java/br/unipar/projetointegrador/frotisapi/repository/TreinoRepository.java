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

    // --- INÍCIO DA CORREÇÃO ---

    // Consulta Antiga (COM ERRO):
    // @Query("SELECT DISTINCT t FROM Treino t LEFT JOIN FETCH t.itensTreino LEFT JOIN FETCH t.alunos WHERE t.id = :id")
    // Consulta Nova (CORRIGIDA):
    @Query("SELECT DISTINCT t FROM Treino t " +
            "LEFT JOIN FETCH t.itensTreino " +
            "LEFT JOIN FETCH t.fichaTreino f " + // Busca a ficha...
            "LEFT JOIN FETCH f.aluno " +          // ...e o aluno da ficha
            "WHERE t.id = :id")
    Optional<Treino> findTreinoCompletoById(@Param("id") Long id);


    // Consulta Antiga (COM ERRO):
    // @Query("SELECT t FROM Treino t LEFT JOIN FETCH t.itensTreino LEFT JOIN FETCH t.alunos WHERE t.diaSemana = :diaSemana")
    // Consulta Nova (CORRIGIDA):
    @Query("SELECT t FROM Treino t " +
            "LEFT JOIN FETCH t.itensTreino " +
            "LEFT JOIN FETCH t.fichaTreino f " +
            "LEFT JOIN FETCH f.aluno " +
            "WHERE t.diaSemana = :diaSemana")
    Optional<Treino> findTreinoCompletoByDiaSemana(@Param("diaSemana") String diaSemana);


    // Consulta Antiga (COM ERRO):
    // @Query("SELECT DISTINCT t FROM Treino t LEFT JOIN FETCH t.itensTreino LEFT JOIN FETCH t.alunos")
    // Consulta Nova (CORRIGIDA):
    @Query("SELECT DISTINCT t FROM Treino t " +
            "LEFT JOIN FETCH t.itensTreino " +
            "LEFT JOIN FETCH t.fichaTreino f " +
            "LEFT JOIN FETCH f.aluno")
    List<Treino> findAllTreinosCompletos();

    // --- FIM DA CORREÇÃO ---
}